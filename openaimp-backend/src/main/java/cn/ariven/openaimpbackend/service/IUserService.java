package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.entity.AtcHistory;
import cn.ariven.openaimpbackend.entity.PilotHistory;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.dto.ChangePassword;
import cn.ariven.openaimpbackend.dto.ResponseMessage;

import java.util.List;

public interface IUserService {
    ResponseMessage<User> getUserInfo(String userCid);
    ResponseMessage<Boolean> changePasswordMethod(ChangePassword changePassword);
    ResponseMessage<Object> getAtcRating();
    ResponseMessage<Boolean> updateAtcRating(int atcRating);
    ResponseMessage<User> getUserByCallsign(String callsign);
    
    // New methods
    ResponseMessage<String> checkUserQQ(String userCid);
    ResponseMessage<String> setUserQQ(String userCid, String qq);
    ResponseMessage<String> setUserStudentId(String userCid, String studentId);
    ResponseMessage<List<PilotHistory>> getUserConnectedHistory(String userCid);
    ResponseMessage<List<AtcHistory>> getUserAtcConnectedHistory(String userCid);
}
