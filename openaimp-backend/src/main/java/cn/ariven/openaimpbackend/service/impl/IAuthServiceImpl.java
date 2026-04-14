package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthRegisterEmail;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.service.AuthService;
import cn.dev33.satoken.secure.SaSecureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IAuthServiceImpl implements AuthService {
  private final AuthMapper authMapper;

  @Override
  public Result<ResponseAuthRegisterEmail> registerByEmail(
      RequestAuthRegisterEmail requestAuthRegisterEmail) {
    if (authMapper.existsByEmail(requestAuthRegisterEmail.getEmail())) {
      return Result.fail("此邮箱已被注册,请直接登陆使用!");
    }
    String encodedPassword = SaSecureUtil.sha256(requestAuthRegisterEmail.getPassword());

    Auth auth =
        Auth.builder().email(requestAuthRegisterEmail.getEmail()).password(encodedPassword).build();
    authMapper.save(auth);
    if (authMapper.existsByEmail(requestAuthRegisterEmail.getEmail())) {
      Auth registeredAuth = authMapper.findAuthByEmail(requestAuthRegisterEmail.getEmail());
      ResponseAuthRegisterEmail responseAuthRegisterEmail =
          ResponseAuthRegisterEmail.builder()
              .cid(registeredAuth.getCid())
              .email(auth.getEmail())
              .build();
      return Result.success("注册成功", responseAuthRegisterEmail);
    }

    return null;
  }
}
