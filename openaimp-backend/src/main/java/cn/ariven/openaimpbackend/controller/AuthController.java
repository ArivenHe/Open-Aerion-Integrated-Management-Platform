package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginCid;
import cn.ariven.openaimpbackend.dto.request.RequestAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.request.RequestAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthLoginEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseAuthRegisterEmail;
import cn.ariven.openaimpbackend.dto.response.ResponseCurrentAuthorization;
import cn.ariven.openaimpbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register/email")
  public Result<ResponseAuthRegisterEmail> registerByEmail(
      @RequestBody RequestAuthRegisterEmail requestAuthRegisterEmail) {
    return authService.registerByEmail(requestAuthRegisterEmail);
  }

  @PostMapping("/login/email")
  public Result<ResponseAuthLoginEmail> loginEmail(
      @RequestBody RequestAuthLoginEmail requestAuthLoginEmail) {
    return authService.loginEmail(requestAuthLoginEmail);
  }

  @PostMapping("/login/cid")
  public Result<ResponseAuthLoginEmail> loginCid(
      @RequestBody RequestAuthLoginCid requestAuthLoginCid) {
    return authService.loginCid(requestAuthLoginCid);
  }

  @GetMapping("/me")
  public Result<ResponseCurrentAuthorization> currentAuthorization() {
    return authService.currentAuthorization();
  }
}
