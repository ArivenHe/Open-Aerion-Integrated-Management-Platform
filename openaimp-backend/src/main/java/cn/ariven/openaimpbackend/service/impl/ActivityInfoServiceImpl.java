package cn.ariven.openaimpbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ariven.openaimpbackend.dto.RegisterAtcDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivityPilot;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.repository.ActivityInfoRepository;
import cn.ariven.openaimpbackend.repository.ActivityPilotRepository;
import cn.ariven.openaimpbackend.repository.ActivitySeatInfoRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.IActivityInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityInfoServiceImpl implements IActivityInfoService {

    private final ActivityInfoRepository activityInfoRepository;
    private final ActivityPilotRepository activityPilotRepository;
    private final ActivitySeatInfoRepository activitySeatInfoRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseMessage<ActivityInfo> getActivityInfo(Integer id) {
        try {
            Optional<ActivityInfo> activityInfo = activityInfoRepository.findById(id);
            return activityInfo.map(ResponseMessage::success)
                    .orElseGet(() -> ResponseMessage.error("活动不存在！"));
        } catch (Exception e) {
            log.error("Error fetching activity info", e);
            return ResponseMessage.error(500, "Internal Server Error");
        }
    }

    @Override
    public ResponseMessage<List<ActivitySeatInfo>> getActivitySeatInfo(int id) {
        log.info("Fetching activity seat info for ID: {}", id);
        List<ActivitySeatInfo> activitySeatInfo = activitySeatInfoRepository.findActivitySeatInfoByActivityId(id);
        if (activitySeatInfo.isEmpty()) {
            log.info("No seat info found for activity ID: {}", id);
        } else {
            log.info(activitySeatInfo.toString());
        }
        return ResponseMessage.success(activitySeatInfo);
    }

    @Override
    public ResponseMessage<Boolean> checkUserNotRegistered(Integer id) {
        String userCid = (String) StpUtil.getLoginId();
        log.info("Checking registration for user CID: {}", userCid);

        int cid = Integer.parseInt(userCid);
        ActivityPilot pilot = activityPilotRepository.findPilotByActivityId(id, cid);
        return ResponseMessage.success(pilot == null);
    }

    @Override
    public ResponseMessage<String> addUser(ActivityPilot pilot) {
        // Check activity existence
        Optional<ActivityInfo> activityOpt = activityInfoRepository.findById(pilot.getActivity_id());
        if (activityOpt.isEmpty()) {
            return ResponseMessage.error("活动不存在");
        }
        ActivityInfo activity = activityOpt.get();

        // Check deadline
        LocalDate deadlineDate = LocalDate.parse(activity.getTime());
        LocalDate today = LocalDate.now();
        if (today.isAfter(deadlineDate)) {
            return ResponseMessage.error("报名时间已截止");
        }

        // Check registration
        String userCid = (String) StpUtil.getLoginId();
        int cid = Integer.parseInt(userCid);
        ActivityPilot existingPilot = activityPilotRepository.findPilotByActivityId(pilot.getActivity_id(), cid);
        if (existingPilot != null) {
            return ResponseMessage.error("已报名无法重复报名!");
        }

        // Set user details
        pilot.setUser_cid(cid);
        User user = userRepository.findById(cid).orElseThrow(() -> new RuntimeException("User not found"));
        pilot.setUser_callsign(user.getCallsign());

        ActivityPilot pilotPojo = new ActivityPilot();
        BeanUtils.copyProperties(pilot, pilotPojo);
        activityPilotRepository.save(pilotPojo);
        return ResponseMessage.success("成功");
    }

    @Override
    @Transactional
    public ResponseMessage<String> deleteUser(ActivityPilot pilot) {
        String userCid = (String) StpUtil.getLoginId();
        int cid = Integer.parseInt(userCid);
        
        ActivityPilot existingPilot = activityPilotRepository.findPilotByActivityId(pilot.getActivity_id(), cid);
        if (existingPilot == null) {
             return ResponseMessage.error("未报名!");
        }

        pilot.setUser_cid(cid);
        int deletedCount = activityPilotRepository.deletePilotByUserId(pilot.getUser_cid(), pilot.getActivity_id());
        return deletedCount > 0 ? ResponseMessage.success("成功") : ResponseMessage.error("删除失败");
    }

    @Override
    public ResponseMessage<List<ActivityInfo>> getAllActivity() {
        log.info("Fetching all activities");
        return ResponseMessage.success((List<ActivityInfo>) activityInfoRepository.findAll());
    }

    @Override
    public ResponseMessage<List<ActivityPilot>> getActivityAllPilot(int id) {
        log.info("Fetching activity pilots for ID: {}", id);
        return ResponseMessage.success(activityPilotRepository.findPilotsByActivityId(id));
    }

    @Override
    public ResponseMessage<Boolean> regAtc(RegisterAtcDTO atcDTO) {
        String userCid = (String) StpUtil.getLoginId();
        int cid = Integer.parseInt(userCid);
        
        Optional<ActivitySeatInfo> seatOpt = activitySeatInfoRepository.findById(atcDTO.getSeatId());
        if (seatOpt.isPresent()) {
            ActivitySeatInfo seat = seatOpt.get();
            if (seat.getUserCid() != null) {
                return ResponseMessage.error("席位已被占用");
            }
            seat.setUserCid(cid);
            
            // Get user callsign
            User user = userRepository.findById(cid).orElse(null);
            if (user != null) {
                seat.setUserCallsign(user.getCallsign());
            }
            
            activitySeatInfoRepository.save(seat);
            return ResponseMessage.success(true);
        }
        return ResponseMessage.error("席位不存在");
    }

    @Override
    public ResponseMessage<Boolean> cancelAtc(int id) {
        String userCid = (String) StpUtil.getLoginId();
        int cid = Integer.parseInt(userCid);

        Optional<ActivitySeatInfo> seatOpt = activitySeatInfoRepository.findById(id);
        if (seatOpt.isPresent()) {
            ActivitySeatInfo seat = seatOpt.get();
            if (seat.getUserCid() != null && seat.getUserCid() == cid) {
                seat.setUserCid(null);
                seat.setUserCallsign(null);
                activitySeatInfoRepository.save(seat);
                return ResponseMessage.success(true);
            }
            return ResponseMessage.error("无权取消或未报名");
        }
        return ResponseMessage.error("席位不存在");
    }
}
