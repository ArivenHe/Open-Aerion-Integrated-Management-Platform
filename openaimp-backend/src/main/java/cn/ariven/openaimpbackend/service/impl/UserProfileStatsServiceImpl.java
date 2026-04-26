package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.mapper.ControlRecordMapper;
import cn.ariven.openaimpbackend.mapper.FlightRecordMapper;
import cn.ariven.openaimpbackend.mapper.UserMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.service.UserProfileStatsService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileStatsServiceImpl implements UserProfileStatsService {
  private final AuthMapper authMapper;
  private final UserMapper userMapper;
  private final FlightRecordMapper flightRecordMapper;
  private final ControlRecordMapper controlRecordMapper;

  @Override
  @Transactional
  public User ensureProfile(Integer userCid) {
    requirePositive(userCid, "userCid");
    Auth auth =
        authMapper.findById(userCid).orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userCid));

    return userMapper
        .findById(userCid)
        .map(
            profile -> {
              boolean changed = false;
              if (profile.getNickname() == null || profile.getNickname().trim().isEmpty()) {
                profile.setNickname(resolveNickname(auth.getEmail(), userCid));
                changed = true;
              }
              if (profile.getCreatedAt() == null) {
                profile.setCreatedAt(resolveCreatedAt(auth));
                changed = true;
              }
              if (profile.getUpdatedAt() == null) {
                profile.setUpdatedAt(resolveCreatedAt(auth));
                changed = true;
              }
              if (profile.getFlightRecordCount() == null) {
                profile.setFlightRecordCount(0);
                changed = true;
              }
              if (profile.getFlightDurationMinutes() == null) {
                profile.setFlightDurationMinutes(0L);
                changed = true;
              }
              if (profile.getControlRecordCount() == null) {
                profile.setControlRecordCount(0);
                changed = true;
              }
              if (profile.getControlDurationMinutes() == null) {
                profile.setControlDurationMinutes(0L);
                changed = true;
              }
              return changed ? userMapper.save(profile) : profile;
            })
        .orElseGet(
            () ->
                userMapper.save(
                    User.builder()
                        .cid(userCid)
                        .nickname(resolveNickname(auth.getEmail(), userCid))
                        .flightRecordCount(0)
                        .flightDurationMinutes(0L)
                        .controlRecordCount(0)
                        .controlDurationMinutes(0L)
                        .createdAt(resolveCreatedAt(auth))
                        .updatedAt(resolveCreatedAt(auth))
                        .build()));
  }

  @Override
  @Transactional
  public void refreshStats(Integer userCid) {
    User profile = ensureProfile(userCid);
    profile.setFlightRecordCount(Math.toIntExact(flightRecordMapper.countByUserCid(userCid)));
    profile.setFlightDurationMinutes(
        defaultLong(flightRecordMapper.sumDurationMinutesByUserCid(userCid)));
    profile.setControlRecordCount(Math.toIntExact(controlRecordMapper.countByUserCid(userCid)));
    profile.setControlDurationMinutes(
        defaultLong(controlRecordMapper.sumDurationMinutesByUserCid(userCid)));
    profile.setUpdatedAt(LocalDateTime.now());
    userMapper.save(profile);
  }

  private Integer requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " 必须为正整数");
    }
    return value;
  }

  private Long defaultLong(Long value) {
    return value == null ? 0L : value;
  }

  private LocalDateTime resolveCreatedAt(Auth auth) {
    return auth.getRegisteredAt() != null ? auth.getRegisteredAt() : LocalDateTime.now();
  }

  private String resolveNickname(String email, Integer userCid) {
    if (email != null) {
      String normalized = email.trim();
      if (!normalized.isEmpty()) {
        int separatorIndex = normalized.indexOf('@');
        if (separatorIndex > 0) {
          return normalized.substring(0, separatorIndex).trim();
        }
        return normalized;
      }
    }
    return "user" + userCid;
  }
}
