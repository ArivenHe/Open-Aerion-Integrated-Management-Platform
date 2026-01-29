package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.RequestForgotPassword;
import cn.ariven.openaimpbackend.dto.request.RequestLogin;
import cn.ariven.openaimpbackend.dto.request.RequestRegister;
import cn.ariven.openaimpbackend.dto.request.RequestResetPassword;
import cn.ariven.openaimpbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RequestRegister request) {
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid RequestLogin request) {
        String token = userService.login(request);
        return Result.success(token);
    }

    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestBody @Valid RequestForgotPassword request) {
        userService.sendForgotPasswordCode(request.getEmail());
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid RequestResetPassword request) {
        userService.resetPassword(request);
        return Result.success();
    }
}
