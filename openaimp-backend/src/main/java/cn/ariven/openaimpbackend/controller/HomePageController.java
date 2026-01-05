package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.service.IActivityInfoService;
import cn.ariven.openaimpbackend.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/homePage")
@RequiredArgsConstructor
public class HomePageController {
    private final IActivityInfoService activityInfoService;
    private final IPlatformService platformService;

    @GetMapping("/onlineUser")
    public ResponseMessage<Map<String, Object>> onlineUser() {
        return platformService.getWhazzup();
    }

    @GetMapping("/getAllActivity")
    public ResponseMessage<List<ActivityInfo>> getAllActivity() {
        return activityInfoService.getAllActivity();
    }
}
