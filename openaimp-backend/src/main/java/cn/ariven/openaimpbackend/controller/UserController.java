package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.AtcHistory;
import cn.ariven.openaimpbackend.entity.PilotHistory;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@SaCheckRole("user-normal")
@RestController
@RequestMapping("/User")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/UserInfo")
    public ResponseMessage<User> getUserInfo() {
        String userCid = (String) StpUtil.getLoginId();
        return userService.getUserInfo(userCid);
    }

    @GetMapping("/UserQQ")
    public ResponseMessage<String> CheckUserQQ() {
        String userCid = (String) StpUtil.getLoginId();
        return userService.checkUserQQ(userCid);
    }

    @PostMapping("/UserQQ")
    public ResponseMessage<String> SetUserQQ(@RequestBody User user) {
        String userCid = (String) StpUtil.getLoginId();
        return userService.setUserQQ(userCid, user.getQq());
    }

    @PostMapping("/UserStudentId")
    public ResponseMessage<String> SetUserStudentId(@RequestBody User user) {
        String userCid = (String) StpUtil.getLoginId();
        return userService.setUserStudentId(userCid, user.getStudentId());
    }

    @GetMapping("/UserPilotConnectedHistory")
    public ResponseMessage<List<PilotHistory>> getUserConnectedHistory() {
        String userCid = (String) StpUtil.getLoginId();
        return userService.getUserConnectedHistory(userCid);
    }

    @GetMapping("/UserAtcConnectedHistory")
    public ResponseMessage<List<AtcHistory>> getUserAtcConnectedHistory() {
        String userCid = (String) StpUtil.getLoginId();
        return userService.getUserAtcConnectedHistory(userCid);
    }
}
