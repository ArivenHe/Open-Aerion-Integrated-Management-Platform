package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.RegisterAtcDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivityPilot;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;

import java.util.List;

public interface IActivityInfoService {
    ResponseMessage<ActivityInfo> getActivityInfo(Integer id);
    ResponseMessage<Boolean> checkUserNotRegistered(Integer id);
    ResponseMessage<String> addUser(ActivityPilot pilot);
    ResponseMessage<String> deleteUser(ActivityPilot pilot);
    ResponseMessage<List<ActivityInfo>> getAllActivity();
    ResponseMessage<List<ActivityPilot>> getActivityAllPilot(int id);
    ResponseMessage<Boolean> regAtc(RegisterAtcDTO atcDTO);
    ResponseMessage<Boolean> cancelAtc(int id);
    
    // New method
    ResponseMessage<List<ActivitySeatInfo>> getActivitySeatInfo(int id);
}
