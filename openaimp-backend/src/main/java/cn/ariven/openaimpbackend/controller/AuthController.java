package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.*;
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
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request);
        return Result.success(token);
    }

    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userService.sendForgotPasswordCode(request.getEmail());
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return Result.success();
    }
}
