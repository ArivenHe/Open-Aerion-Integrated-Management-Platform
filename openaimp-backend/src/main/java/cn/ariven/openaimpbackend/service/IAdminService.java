package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ChangeUserRatingDTO;
import cn.ariven.openaimpbackend.dto.ChangeUserRoleDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.entity.UserRole;

import java.util.List;

public interface IAdminService {
    ResponseMessage<String> changeUserRating(ChangeUserRatingDTO changeUserRatingDTO);
    ResponseMessage<List<User>> getAllUser();
    ResponseMessage<List<UserRole>> getUserRole();
    ResponseMessage<String> changeUserRole(ChangeUserRoleDTO changeUserRoleDTO);
    ResponseMessage<Integer> eventPost(ActivityInfo activityInfo);
    ResponseMessage<Integer> eventAtcPost(List<ActivitySeatInfo> activitySeatInfoList);
}
