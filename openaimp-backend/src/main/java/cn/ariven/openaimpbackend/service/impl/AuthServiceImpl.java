package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.constant.FsdConstants;
import cn.ariven.openaimpbackend.constant.RbacConstants;
import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginCid;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.request.RequestAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.request.RequestFsdCreateUser;
import cn.ariven.openaimpbackend.dto.request.RequestFsdUpdateUser;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.dto.response.ResponseFsdUser;
import cn.ariven.openaimpbackend.mapper.AtcMapper;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.mapper.UserMapper;
import cn.ariven.openaimpbackend.pojo.Atc;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.User;
import java.time.LocalDateTime;
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
  private final AtcMapper atcMapper;
  private final UserMapper userMapper;

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

    LocalDateTime now = LocalDateTime.now();
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
            fsdUser.getCid(),
            requestAuthRegisterEmail.getEmail(),
            encodedPassword,
            now,
            null);
    if (inserted > 0) {
      Auth registeredAuth = authMapper.findAuthByEmail(requestAuthRegisterEmail.getEmail());
      ensureUserProfile(registeredAuth, requestAuthRegisterEmail.getFirstName(), requestAuthRegisterEmail.getLastName());
      rbacService.ensureDefaultRolesForNewUser(registeredAuth);
      Integer netRating = resolveDefaultAtcNetworkRating(registeredAuth.getCid());
      if (netRating >= FsdConstants.NETWORK_RATING_ADMINISTRATOR) {
        fsdService.updateUser(
            RequestFsdUpdateUser.builder()
                .cid(registeredAuth.getCid())
                .networkRating(netRating)
                .build());
      }
      ensureAtcBinding(registeredAuth.getCid());
      ResponseAuthRegisterEmail responseAuthRegisterEmail =
          ResponseAuthRegisterEmail.builder()
              .cid(registeredAuth.getCid())
              .email(registeredAuth.getEmail())
              .registeredAt(registeredAuth.getRegisteredAt())
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
      touchLastLoginAt(auth);
      StpUtil.login(auth.getCid());
      return Result.success("登录成功", buildLoginResponse(auth));
    }

    return Result.fail("邮箱或密码错误");
  }

  @Override
  public Result<ResponseAuthLoginEmail> loginCid(RequestAuthLoginCid requestAuthLoginCid) {
    if (requestAuthLoginCid == null || requestAuthLoginCid.getCid() == null) {
      return Result.fail("CID 不能为空");
    }

    Auth auth =
        authMapper
            .findById(requestAuthLoginCid.getCid())
            .orElse(null);
    if (auth == null) {
      return Result.fail("CID 不存在");
    }

    touchLastLoginAt(auth);
    StpUtil.login(auth.getCid());
    return Result.success("登录成功", buildLoginResponse(auth));
  }

  @Override
  public Result<ResponseCurrentAuthorization> currentAuthorization() {
    StpUtil.checkLogin();
    return Result.success("获取当前权限信息成功", rbacService.getCurrentUserAuthorization());
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private ResponseAuthLoginEmail buildLoginResponse(Auth auth) {
    ResponseCurrentAuthorization authorization = rbacService.getUserAuthorization(auth.getCid());
    Atc atc = ensureAtcBinding(auth.getCid());
    User user = ensureUserProfile(auth, null, null);
    return ResponseAuthLoginEmail.builder()
        .cid(auth.getCid())
        .token(StpUtil.getTokenValue())
        .user(
            ResponseAuthLoginEmail.UserInfo.builder()
                .cid(auth.getCid())
                .email(auth.getEmail())
                .rating(atc.getNetworkRating())
                .nickname(user.getNickname())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .flightRecordCount(defaultInteger(user.getFlightRecordCount()))
                .flightDurationMinutes(defaultLong(user.getFlightDurationMinutes()))
                .controlRecordCount(defaultInteger(user.getControlRecordCount()))
                .controlDurationMinutes(defaultLong(user.getControlDurationMinutes()))
                .registeredAt(auth.getRegisteredAt())
                .lastLoginAt(auth.getLastLoginAt())
                .netRating(atc.getNetworkRating())
                .build())
        .rbac(
            ResponseAuthLoginEmail.RbacInfo.builder()
                .roles(authorization.getRoles())
                .permissions(authorization.getPermissions())
                .build())
        .build();
  }

  private void touchLastLoginAt(Auth auth) {
    auth.setLastLoginAt(LocalDateTime.now());
    authMapper.save(auth);
  }

  private User ensureUserProfile(Auth auth, String firstName, String lastName) {
    return userMapper
        .findById(auth.getCid())
        .map(
            existing -> {
              boolean changed = false;
              LocalDateTime now = LocalDateTime.now();
              if (isBlank(existing.getNickname())) {
                existing.setNickname(resolveNickname(auth.getEmail(), auth.getCid()));
                changed = true;
              }
              if (isBlank(existing.getFirstName()) && !isBlank(firstName)) {
                existing.setFirstName(firstName.trim());
                changed = true;
              }
              if (isBlank(existing.getLastName()) && !isBlank(lastName)) {
                existing.setLastName(lastName.trim());
                changed = true;
              }
              if (existing.getUpdatedAt() == null) {
                existing.setUpdatedAt(now);
                changed = true;
              }
              if (existing.getCreatedAt() == null) {
                existing.setCreatedAt(resolveProfileCreatedAt(auth));
                changed = true;
              }
              if (existing.getFlightRecordCount() == null) {
                existing.setFlightRecordCount(0);
                changed = true;
              }
              if (existing.getFlightDurationMinutes() == null) {
                existing.setFlightDurationMinutes(0L);
                changed = true;
              }
              if (existing.getControlRecordCount() == null) {
                existing.setControlRecordCount(0);
                changed = true;
              }
              if (existing.getControlDurationMinutes() == null) {
                existing.setControlDurationMinutes(0L);
                changed = true;
              }
              if (changed) {
                existing.setUpdatedAt(now);
              }
              return changed ? userMapper.save(existing) : existing;
            })
        .orElseGet(
            () -> {
              LocalDateTime now = LocalDateTime.now();
              return userMapper.save(
                  User.builder()
                      .cid(auth.getCid())
                      .nickname(resolveNickname(auth.getEmail(), auth.getCid()))
                      .firstName(normalizeNullable(firstName))
                      .lastName(normalizeNullable(lastName))
                      .avatar(null)
                      .bio(null)
                      .flightRecordCount(0)
                      .flightDurationMinutes(0L)
                      .controlRecordCount(0)
                      .controlDurationMinutes(0L)
                      .createdAt(resolveProfileCreatedAt(auth))
                      .updatedAt(now)
                      .build());
            });
  }

  private Atc ensureAtcBinding(Integer userId) {
    return atcMapper
        .findByUserId(userId)
        .orElseGet(
            () ->
                atcMapper.save(
                    Atc.builder()
                        .userId(userId)
                        .networkRating(resolveDefaultAtcNetworkRating(userId))
                        .facilityType(0)
                        .enabled(Boolean.TRUE)
                        .build()));
  }

  private Integer resolveDefaultAtcNetworkRating(Integer userId) {
    return rbacService.getRoleCodesByUserId(userId).contains(RbacConstants.ROLE_SUPER_ADMIN)
        ? FsdConstants.NETWORK_RATING_ADMINISTRATOR
        : FsdConstants.NETWORK_RATING_OBSERVER;
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

  private LocalDateTime resolveProfileCreatedAt(Auth auth) {
    return auth.getRegisteredAt() != null ? auth.getRegisteredAt() : LocalDateTime.now();
  }

  private String resolveNickname(String email, Integer cid) {
    if (!isBlank(email)) {
      int separatorIndex = email.indexOf('@');
      if (separatorIndex > 0) {
        return email.substring(0, separatorIndex).trim();
      }
      return email.trim();
    }
    return "user" + cid;
  }

  private String normalizeNullable(String value) {
    return isBlank(value) ? null : value.trim();
  }

  private Integer defaultInteger(Integer value) {
    return value == null ? 0 : value;
  }

  private Long defaultLong(Long value) {
    return value == null ? 0L : value;
  }
}
