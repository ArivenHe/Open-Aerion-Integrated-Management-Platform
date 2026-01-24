package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.*;

public interface UserService {
    void register(RegisterRequest request);
    String login(LoginRequest request);
    void sendForgotPasswordCode(String email);
    void resetPassword(ResetPasswordRequest request);
}
