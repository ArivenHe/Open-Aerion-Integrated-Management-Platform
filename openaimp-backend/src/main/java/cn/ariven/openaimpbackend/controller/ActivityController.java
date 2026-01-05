package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.ariven.openaimpbackend.dto.RegisterAtcDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivityPilot;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import cn.ariven.openaimpbackend.service.IActivityInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@SaCheckRole("user-normal")
@RequestMapping("/Activity")
public class ActivityController {

    private final IActivityInfoService activityInfoService;

    @GetMapping("/ActivityInfo")
    public ResponseMessage<ActivityInfo> getActivityInfo(@RequestParam int id) {
        return activityInfoService.getActivityInfo(id);
    }

    @GetMapping("/ActivitySeatInfo")
    public ResponseMessage<List<ActivitySeatInfo>> getActivitySeatInfo(@RequestParam int id) {
        return activityInfoService.getActivitySeatInfo(id);
    }

    @GetMapping("/CheckPilotIsNotRegistered")
    public ResponseMessage<Boolean> checkPilotIsNotRegistered(@RequestParam int id) {
        return activityInfoService.checkUserNotRegistered(id);
    }

    @PostMapping("/RegisterPilot")
    public ResponseMessage<String> registerPilot(@RequestBody ActivityPilot pilot) {
        return activityInfoService.addUser(pilot);
    }

    @PostMapping("/DeleteRegisteredPilot")
    public ResponseMessage<String> deleteRegisteredPilot(@RequestBody ActivityPilot pilot) {
        return activityInfoService.deleteUser(pilot);
    }

    @GetMapping("/GetAllActivity")
    public ResponseMessage<List<ActivityInfo>> getAllActivity() {
        return activityInfoService.getAllActivity();
    }

    @GetMapping("/GetActivityPilot")
    public ResponseMessage<List<ActivityPilot>> getActivityPilot(@RequestParam int id) {
        return activityInfoService.getActivityAllPilot(id);
    }

    @PostMapping("/RegisterAtc")
    public ResponseMessage<Boolean> registerAtc(@RequestBody RegisterAtcDTO atcDTO) {
        return activityInfoService.regAtc(atcDTO);
    }

    @GetMapping("/CancelAtc")
    public ResponseMessage<Boolean> cancelAtc(@RequestParam int id) {
        return activityInfoService.cancelAtc(id);
    }
}
