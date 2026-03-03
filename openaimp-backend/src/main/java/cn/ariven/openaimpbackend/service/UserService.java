package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.request.auth.RequestLogin;
import cn.ariven.openaimpbackend.dto.request.auth.RequestRegister;
import cn.ariven.openaimpbackend.dto.request.auth.RequestResetPassword;
import cn.ariven.openaimpbackend.dto.response.user.ResponseOnlineStats;

public interface UserService {
    void register(RequestRegister request);
    String login(RequestLogin request);
    void logout();
    ResponseOnlineStats getOnlineStats();
    void sendRegisterCode(String email);
    void sendForgotPasswordCode(String email);
    void resetPassword(RequestResetPassword request);
}
