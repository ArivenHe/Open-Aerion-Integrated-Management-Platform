package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.constant.FsdConstants;
import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.request.RequestAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.request.RequestFsdCreateUser;
import cn.ariven.openaimpbackend.dto.request.RequestFsdUpdateUser;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdUser;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.service.AuthService;
import cn.ariven.openaimpbackend.service.CaptchaService;
import cn.ariven.openaimpbackend.service.FsdService;
import cn.ariven.openaimpbackend.service.RbacService;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final AuthMapper authMapper;
  private final CaptchaService captchaService;
  private final RbacService rbacService;
  private final FsdService fsdService;

  @Override
  @Transactional
  public Result<ResponseAuthRegisterEmail> registerByEmail(
      RequestAuthRegisterEmail requestAuthRegisterEmail) {
    if (requestAuthRegisterEmail == null
        || isBlank(requestAuthRegisterEmail.getEmail())
        || isBlank(requestAuthRegisterEmail.getPassword())
        || isBlank(requestAuthRegisterEmail.getEmailCode())) {
      return Result.fail("邮箱、密码和邮箱验证码不能为空");
    }

    if (authMapper.existsByEmail(requestAuthRegisterEmail.getEmail())) {
      return Result.fail("此邮箱已被注册,请直接登陆使用!");
    }

    if (!captchaService.validateEmailCode(
        requestAuthRegisterEmail.getEmail(), requestAuthRegisterEmail.getEmailCode())) {
      return Result.fail("邮箱验证码错误或已过期");
    }

    String encodedPassword = SaSecureUtil.sha256(requestAuthRegisterEmail.getPassword());
    ResponseFsdUser fsdUser =
        fsdService.createUser(
            RequestFsdCreateUser.builder()
                .password(requestAuthRegisterEmail.getPassword())
                .firstName(
                    resolveFsdFirstName(
                        requestAuthRegisterEmail.getFirstName(),
                        requestAuthRegisterEmail.getEmail()))
                .lastName(requestAuthRegisterEmail.getLastName())
                .build());

    if (authMapper.existsById(fsdUser.getCid())) {
      throw new IllegalArgumentException("本地用户 CID 与 FSD 账号冲突，请联系管理员处理: " + fsdUser.getCid());
    }

    int inserted =
        authMapper.insertWithCid(
            fsdUser.getCid(), requestAuthRegisterEmail.getEmail(), encodedPassword);
    if (inserted > 0) {
      Auth registeredAuth = authMapper.findAuthByEmail(requestAuthRegisterEmail.getEmail());
      rbacService.ensureDefaultRolesForNewUser(registeredAuth);
      if (rbacService.getRoleCodesByUserId(registeredAuth.getCid()).contains(RbacConstants.ROLE_SUPER_ADMIN)) {
        fsdService.updateUser(
            RequestFsdUpdateUser.builder()
                .cid(registeredAuth.getCid())
                .networkRating(FsdConstants.NETWORK_RATING_ADMINISTRATOR)
                .build());
      }
      ResponseAuthRegisterEmail responseAuthRegisterEmail =
          ResponseAuthRegisterEmail.builder()
              .cid(registeredAuth.getCid())
              .email(registeredAuth.getEmail())
              .build();
      return Result.success("注册成功，平台账号与 FSD 账号已同步创建", responseAuthRegisterEmail);
    }

    return Result.fail("注册失败,请稍后重试");
  }

  @Override
  public Result<ResponseAuthLoginEmail> loginEmail(RequestAuthLoginEmail requestAuthLoginEmail) {

    if (requestAuthLoginEmail == null
        || isBlank(requestAuthLoginEmail.getEmail())
        || isBlank(requestAuthLoginEmail.getPassword())) {
      return Result.fail("邮箱或密码不能为空");
    }

    Auth auth = authMapper.findAuthByEmail(requestAuthLoginEmail.getEmail());
    if (auth != null
        && auth.getPassword().equals(SaSecureUtil.sha256(requestAuthLoginEmail.getPassword()))) {
      StpUtil.login(auth.getCid());
      return Result.success("登录成功", ResponseAuthLoginEmail.builder().cid(auth.getCid()).build());
    }

    return Result.fail("邮箱或密码错误");
  }

  @Override
  public Result<ResponseCurrentAuthorization> currentAuthorization() {
    StpUtil.checkLogin();
    return Result.success("获取当前权限信息成功", rbacService.getCurrentUserAuthorization());
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String resolveFsdFirstName(String firstName, String email) {
    if (!isBlank(firstName)) {
      return firstName.trim();
    }
    if (!isBlank(email)) {
      int separatorIndex = email.indexOf('@');
      if (separatorIndex > 0) {
        return email.substring(0, separatorIndex);
      }
      return email.trim();
    }
    return "OpenAIMP";
  }
}
