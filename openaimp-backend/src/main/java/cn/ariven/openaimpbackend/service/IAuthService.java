package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.dto.UserDto;
import cn.ariven.openaimpbackend.dto.ResponseMessage;

public interface IAuthService {
    ResponseMessage<User> addUser(UserDto user);
    ResponseMessage<Boolean> isEmailRegistered(String email);
    ResponseMessage<Boolean> isCallsignRegistered(String callsign);
    ResponseMessage<Boolean> activateUser(String userId);
    ResponseMessage<Boolean> login(String email, String password);
    ResponseMessage<Boolean> forgotPassword(String email, String password);
    ResponseMessage<Boolean> regBbs(String callsign, String password, String email);
    void storeUserToRedis(int userId, User userDetail);
    
    // New methods to handle controller logic
    ResponseMessage<String> sendEmailCode(String clientIp, String email);
    ResponseMessage<String> register(UserDto user);
}
