package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.response.user.ResponseOnlineStats;
import cn.ariven.openaimpbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/online/stats")
    public Result<ResponseOnlineStats> getOnlineStats() {
        return Result.success(userService.getOnlineStats());
    }
}
