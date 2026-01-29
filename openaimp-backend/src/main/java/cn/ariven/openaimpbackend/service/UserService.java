package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.RequestLogin;
import cn.ariven.openaimpbackend.dto.request.RequestRegister;
import cn.ariven.openaimpbackend.dto.request.RequestResetPassword;

public interface UserService {
    void register(RequestRegister request);
    String login(RequestLogin request);
    void sendForgotPasswordCode(String email);
    void resetPassword(RequestResetPassword request);
}
