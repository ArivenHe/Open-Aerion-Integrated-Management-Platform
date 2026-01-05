package cn.ariven.openaimpbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ariven.openaimpbackend.dto.ChangeUserRatingDTO;
import cn.ariven.openaimpbackend.dto.ChangeUserRoleDTO;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.ActivityInfo;
import cn.ariven.openaimpbackend.entity.ActivitySeatInfo;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.entity.UserRole;
import cn.ariven.openaimpbackend.repository.ActivityInfoRepository;
import cn.ariven.openaimpbackend.repository.ActivitySeatInfoRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.repository.UserRoleRepository;
import cn.ariven.openaimpbackend.service.IAdminService;
import cn.ariven.openaimpbackend.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ActivityInfoRepository activityInfoRepository;
    private final ActivitySeatInfoRepository activitySeatInfoRepository;
    private final IEmailService emailService;

    @Override
    public ResponseMessage<String> changeUserRating(ChangeUserRatingDTO changeUserRatingDTO) {
        log.info("Changing user rating: {}", changeUserRatingDTO);

        Optional<User> userOptional = userRepository.findById(changeUserRatingDTO.getUserId());

        if (userOptional.isEmpty()) {
            log.warn("User not found with ID: {}", changeUserRatingDTO.getUserId());
            return ResponseMessage.error("User not found");
        }

        User user = userOptional.get();

        int updatedCount = userRepository.updateRatingByCid(
                changeUserRatingDTO.getUserId(),
                changeUserRatingDTO.getRating()
        );

        if (updatedCount > 0) {
            emailService.sendRatingUpdateEmail(user.getEmail(), this.getRatingText(changeUserRatingDTO.getRating()));
            log.info("Successfully updated rating for user: {}", changeUserRatingDTO.getUserId());
            StpUtil.logout(changeUserRatingDTO.getUserId());
            return ResponseMessage.success("修改成功");
        }

        return ResponseMessage.error("修改失败");
    }

    @Override
    public ResponseMessage<List<User>> getAllUser() {
        log.info("getAllUser");
        return ResponseMessage.success((List<User>) userRepository.findAll());
    }

    @Override
    public ResponseMessage<List<UserRole>> getUserRole() {
        log.info("getUserRole");
        return ResponseMessage.success((List<UserRole>) userRoleRepository.findAll());
    }

    @Override
    public ResponseMessage<String> changeUserRole(ChangeUserRoleDTO changeUserRoleDTO) {
        log.info("changeUserRole");
        log.info("roleList: {}", changeUserRoleDTO);
        Optional<User> userOpt = userRepository.findById(changeUserRoleDTO.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Assuming group is still used for roles in string format as per legacy support in StpInterfaceImpl
            user.setGroup(changeUserRoleDTO.getRoleList());
            userRepository.save(user);
            StpUtil.logout(changeUserRoleDTO.getUserId());
            return ResponseMessage.success("修改成功");
        }
        return ResponseMessage.error("User not found");
    }

    @Override
    public ResponseMessage<Integer> eventPost(ActivityInfo activityInfo) {
        ActivityInfo savedInfo = activityInfoRepository.save(activityInfo);
        return ResponseMessage.success(savedInfo.getId());
    }

    @Override
    public ResponseMessage<Integer> eventAtcPost(List<ActivitySeatInfo> activitySeatInfoList) {
        int savedCount = 0;
        for (ActivitySeatInfo info : activitySeatInfoList) {
            activitySeatInfoRepository.save(info);
            savedCount++;
        }
        return ResponseMessage.success(savedCount);
    }

    private String getRatingText(int rating ) {
        return switch (rating) {
            case 1 -> "OBS/Pilot";
            case 2 -> "STU1";
            case 3 -> "STU2";
            case 4 -> "STU3";
            case 5 -> "CTR1";
            case 6 -> "CTR2";
            case 7 -> "CTR3";
            case 8 -> "INS1";
            case 9 -> "INS2";
            case 10 -> "INS3";
            case 11 -> "SUP";
            case 12 -> "ADM";
            default -> "error!";
        };
    }
}
