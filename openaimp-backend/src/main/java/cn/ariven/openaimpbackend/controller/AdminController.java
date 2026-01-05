package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.ariven.openaimpbackend.dto.ChangeUserRatingDTO;
import cn.ariven.openaimpbackend.dto.ChangeUserRoleDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.entity.UserRole;
import cn.ariven.openaimpbackend.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@SaCheckRole("show-admin-entrance")
@RequestMapping("/ADMIN")
public class AdminController {

    private final IAdminService adminService;

    @SaCheckRole("user-rating-edit")
    @PostMapping("/ChangeUserRating")
    public ResponseMessage<String> changeUserRating(@RequestBody ChangeUserRatingDTO changeUserRatingDTO) {
        return adminService.changeUserRating(changeUserRatingDTO);
    }

    @GetMapping("/GetAllUser")
    public ResponseMessage<List<User>> getAllUser() {
        return adminService.getAllUser();
    }

    @SaCheckRole("super-admin")
    @GetMapping("/GetUserRole")
    public ResponseMessage<List<UserRole>> getUserRole() {
        return adminService.getUserRole();
    }

    @SaCheckRole("super-admin")
    @PostMapping("/ChangeUserRole")
    public ResponseMessage<String> changeUserRole(@RequestBody ChangeUserRoleDTO changeUserRoleDTO) {
        return adminService.changeUserRole(changeUserRoleDTO);
    }

    @SaCheckRole("event-posting")
    @PostMapping("/EventPost")
    public ResponseMessage<Integer> eventPost(@RequestBody ActivityInfo activityInfo) {
        return adminService.eventPost(activityInfo);
    }

    @SaCheckRole("event-posting")
    @PostMapping("/EventAtcPost")
    public ResponseMessage<Integer> eventAtcPost(@RequestBody List<ActivitySeatInfo> activitySeatInfoList) {
        return adminService.eventAtcPost(activitySeatInfoList);
    }
}
