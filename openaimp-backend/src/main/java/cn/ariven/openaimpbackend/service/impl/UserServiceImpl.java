package cn.ariven.openaimpbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ariven.openaimpbackend.dto.ChangePassword;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.AtcHistory;
import cn.ariven.openaimpbackend.entity.PilotHistory;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.repository.OnlineAtcRepository;
import cn.ariven.openaimpbackend.repository.OnlinePilotRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.IUserService;
import cn.ariven.openaimpbackend.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final OnlinePilotRepository onlinePilotRepository;
    private final OnlineAtcRepository onlineAtcRepository;

    @Override
    public ResponseMessage<User> getUserInfo(String userCid) {
        try {
            log.info("开始查询{}", userCid);
            Integer id = Integer.valueOf(userCid);
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword("无权限查看");
                return ResponseMessage.success(user);
            }
            return ResponseMessage.error("用户不存在！");
        } catch (NumberFormatException e) {
            return ResponseMessage.error("Invalid User ID format");
        }
    }

    @Override
    public ResponseMessage<User> getUserByCallsign(String callsign) {
        User user = userRepository.findUserByCallsign(callsign);
        if (user != null) {
            return ResponseMessage.success(user);
        }
        return ResponseMessage.error("User not found");
    }

    @Override
    public ResponseMessage<Boolean> changePasswordMethod(ChangePassword changePassword) {
        log.info(changePassword.toString());
        String userCid = (String) StpUtil.getLoginId();
        Integer cid = Integer.parseInt(userCid);
        log.info(String.valueOf(cid));

        Optional<User> userOptional = userRepository.findById(cid);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info(user.toString());

            boolean isValid = PasswordEncoder.compare(changePassword.getOldPassword(), user.getPassword());
            if (isValid) {
                user.setPassword(PasswordEncoder.encode(changePassword.getNewPassword()));
                userRepository.save(user);
                return ResponseMessage.success(true);
            } else {
                return ResponseMessage.error("Old password incorrect");
            }
        }
        return ResponseMessage.error("User not found");
    }

    @Override
    public ResponseMessage<Object> getAtcRating() {
        String userCid = (String) StpUtil.getLoginId();
        Integer cid = Integer.parseInt(userCid);
        log.info(String.valueOf(cid));

        Optional<User> userOptional = userRepository.findById(cid);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Integer atcRating = user.getAtcRating();
            log.info(String.valueOf(atcRating));

            if (atcRating == null) atcRating = 1; // Default to 1 if null

            String binaryStr = String.format("%8s", Integer.toBinaryString(atcRating))
                    .replace(' ', '0');

            int[] result = binaryStr.chars()
                    .map(c -> c - '0')
                    .toArray();
            return ResponseMessage.success(result);
        }

        return ResponseMessage.error("User not found");
    }

    @Override
    public ResponseMessage<Boolean> updateAtcRating(int atcRating) {
        String userCid = (String) StpUtil.getLoginId();
        Integer cid = Integer.parseInt(userCid);
        log.info(String.valueOf(cid));
        // Logic to update ATC rating if needed
        return ResponseMessage.success(true);
    }

    @Override
    public ResponseMessage<String> checkUserQQ(String userCid) {
        log.info("检查cid:{}", userCid);
        ResponseMessage<User> response = getUserInfo(userCid);
        User user = response.getData();
        if (user != null) {
            String qq = user.getQq();
            return ResponseMessage.success(Objects.requireNonNullElse(qq, "false"));
        }
        return ResponseMessage.error("User not found");
    }

    @Override
    public ResponseMessage<String> setUserQQ(String userCid, String qq) {
        log.info("绑定QQ,cid:{}", userCid);
        if (userRepository.setNewQQ(userCid, qq) > 0) {
            return ResponseMessage.success("true");
        } else {
            return ResponseMessage.success("false");
        }
    }

    @Override
    public ResponseMessage<String> setUserStudentId(String userCid, String studentId) {
        log.info("绑定学号,cid:{}", userCid);
        if (userRepository.setNewStudentId(userCid, studentId) > 0) {
            return ResponseMessage.success("true");
        } else {
            return ResponseMessage.success("false");
        }
    }

    @Override
    public ResponseMessage<List<PilotHistory>> getUserConnectedHistory(String userCid) {
        log.info("Fetching history for user CID: {}", userCid);
        try {
            int cid = Integer.parseInt(userCid);
            List<PilotHistory> pilotHistories = onlinePilotRepository.findPilotHistoriesByCid(cid);
            if (pilotHistories.isEmpty()) {
                log.info("No pilot history found for user CID: {}", cid);
            } else {
                log.info(pilotHistories.toString());
            }
            return ResponseMessage.success(pilotHistories);
        } catch (NumberFormatException e) {
            return ResponseMessage.error("Invalid CID format");
        }
    }

    @Override
    public ResponseMessage<List<AtcHistory>> getUserAtcConnectedHistory(String userCid) {
        try {
            int cid = Integer.parseInt(userCid);
            List<AtcHistory> atcHistories = onlineAtcRepository.findAtcHistoriesByCid(cid);
            return ResponseMessage.success(atcHistories);
        } catch (NumberFormatException e) {
            return ResponseMessage.error("Invalid CID format");
        }
    }
}
