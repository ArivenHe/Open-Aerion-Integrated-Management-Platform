package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.auth.RequestLogin;
import cn.ariven.openaimpbackend.dto.request.auth.RequestRegister;
import cn.ariven.openaimpbackend.dto.request.auth.RequestResetPassword;

public interface UserService {
    void register(RequestRegister request);
    String login(RequestLogin request);
    void sendForgotPasswordCode(String email);
    void resetPassword(RequestResetPassword request);
}
