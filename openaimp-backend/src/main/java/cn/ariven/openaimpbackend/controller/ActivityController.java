package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityCreate;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityId;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityUpdate;
import cn.ariven.openaimpbackend.dto.response.activity.ResponseActivity;
import cn.ariven.openaimpbackend.service.ActivityService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/create")
    @SaCheckPermission("activity:create")
    public Result<ResponseActivity> create(@RequestBody @Valid RequestActivityCreate request) {
        return activityService.create(request);
    }

    @PostMapping("/update")
    @SaCheckPermission("activity:update")
    public Result<ResponseActivity> update(@RequestBody @Valid RequestActivityUpdate request) {
        return activityService.update(request);
    }

    @PostMapping("/delete")
    @SaCheckPermission("activity:delete")
    public Result<Void> delete(@RequestBody @Valid RequestActivityId request) {
        return activityService.delete(request);
    }

    @PostMapping("/detail")
    public Result<ResponseActivity> detail(@RequestBody @Valid RequestActivityId request) {
        return activityService.detail(request);
    }

    @PostMapping("/list")
    public Result<List<ResponseActivity>> list() {
        return activityService.list();
    }

    @PostMapping("/signup/pilot")
    public Result<ResponseActivity> signupPilot(@RequestBody @Valid RequestActivityId request) {
        return activityService.signupPilot(request);
    }

    @PostMapping("/signup/atc")
    public Result<ResponseActivity> signupAtc(@RequestBody @Valid RequestActivityId request) {
        return activityService.signupAtc(request);
    }
}
