package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityCreate;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityId;
import cn.ariven.openaimpbackend.dto.request.activity.RequestActivityUpdate;
import cn.ariven.openaimpbackend.dto.response.activity.ResponseActivity;
import cn.ariven.openaimpbackend.dto.response.activity.ResponseActivityUser;
import cn.ariven.openaimpbackend.pojo.Activity;
import cn.ariven.openaimpbackend.pojo.ActivityDetail;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.repository.ActivityDetailRepository;
import cn.ariven.openaimpbackend.repository.ActivityRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.ActivityService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityDetailRepository activityDetailRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Result<ResponseActivity> create(RequestActivityCreate request) {
        ActivityDetail detail = new ActivityDetail();
        detail.setActivityTime(request.getActivityTime());
        detail.setDepIcao(request.getDepIcao());
        detail.setAppIcao(request.getAppIcao());
        detail.setAirportRating(request.getAirportRating());
        detail.setNotams(request.getNotams());
        detail.setFlightDirection(request.getFlightDirection());
        detail = activityDetailRepository.save(detail);

        Activity activity = new Activity();
        activity.setActivityDetail(detail);
        activity.setPilotList(new ArrayList<>());
        activity.setAtcList(new ArrayList<>());
        activity = activityRepository.save(activity);

        return Result.success(toResponse(activity));
    }

    @Override
    @Transactional
    public Result<ResponseActivity> update(RequestActivityUpdate request) {
        Activity activity = activityRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        ActivityDetail detail = activity.getActivityDetail();
        if (detail == null) {
            detail = new ActivityDetail();
        }
        if (request.getActivityTime() != null) {
            detail.setActivityTime(request.getActivityTime());
        }
        if (request.getDepIcao() != null) {
            detail.setDepIcao(request.getDepIcao());
        }
        if (request.getAppIcao() != null) {
            detail.setAppIcao(request.getAppIcao());
        }
        if (request.getAirportRating() != null) {
            detail.setAirportRating(request.getAirportRating());
        }
        if (request.getNotams() != null) {
            detail.setNotams(request.getNotams());
        }
        if (request.getFlightDirection() != null) {
            detail.setFlightDirection(request.getFlightDirection());
        }

        detail = activityDetailRepository.save(detail);
        activity.setActivityDetail(detail);
        activity = activityRepository.save(activity);
        return Result.success(toResponse(activity));
    }

    @Override
    @Transactional
    public Result<Void> delete(RequestActivityId request) {
        Activity activity = activityRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        ActivityDetail detail = activity.getActivityDetail();
        activityRepository.delete(activity);
        if (detail != null) {
            activityDetailRepository.delete(detail);
        }
        return Result.success();
    }

    @Override
    @Transactional(readOnly = true)
    public Result<ResponseActivity> detail(RequestActivityId request) {
        Activity activity = activityRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        return Result.success(toResponse(activity));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<ResponseActivity>> list() {
        List<ResponseActivity> list = activityRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<ResponseActivity> signupPilot(RequestActivityId request) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getAtcRating() == null || user.getAtcRating() <= 0) {
            throw new RuntimeException("飞行员等级不足，无法报名");
        }
        Activity activity = activityRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        List<User> pilotList = activity.getPilotList();
        if (pilotList == null) {
            pilotList = new ArrayList<>();
        }
        if (pilotList.stream().noneMatch(p -> p.getId().equals(user.getId()))) {
            pilotList.add(user);
        }
        activity.setPilotList(pilotList);
        activity = activityRepository.save(activity);
        return Result.success(toResponse(activity));
    }

    @Override
    @Transactional
    public Result<ResponseActivity> signupAtc(RequestActivityId request) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Activity activity = activityRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));
        List<User> atcList = activity.getAtcList();
        if (atcList == null) {
            atcList = new ArrayList<>();
        }
        // TODO: 细分ATC报名权限校验
        if (atcList.stream().noneMatch(p -> p.getId().equals(user.getId()))) {
            atcList.add(user);
        }
        activity.setAtcList(atcList);
        activity = activityRepository.save(activity);
        return Result.success(toResponse(activity));
    }

    private ResponseActivity toResponse(Activity activity) {
        ActivityDetail detail = activity.getActivityDetail();
        return ResponseActivity.builder()
                .id(activity.getId())
                .activityTime(detail != null ? detail.getActivityTime() : null)
                .depIcao(detail != null ? detail.getDepIcao() : null)
                .appIcao(detail != null ? detail.getAppIcao() : null)
                .airportRating(detail != null ? detail.getAirportRating() : null)
                .notams(detail != null ? detail.getNotams() : null)
                .flightDirection(detail != null ? detail.getFlightDirection() : null)
                .pilotList(toUserList(activity.getPilotList()))
                .atcList(toUserList(activity.getAtcList()))
                .build();
    }

    private List<ResponseActivityUser> toUserList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(user -> new ResponseActivityUser(user.getId(), user.getCallsign()))
                .collect(Collectors.toList());
    }
}
