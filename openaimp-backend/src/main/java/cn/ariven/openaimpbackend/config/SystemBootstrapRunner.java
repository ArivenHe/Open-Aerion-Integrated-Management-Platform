package cn.ariven.openaimpbackend.config;

import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.mapper.PlatformMapper;
import cn.ariven.openaimpbackend.mapper.UserMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.Platform;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.service.UserProfileStatsService;
import java.time.LocalDateTime;
import cn.dev33.satoken.secure.SaSecureUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@Order(10)
@RequiredArgsConstructor
public class SystemBootstrapRunner implements ApplicationRunner {
  private final AuthMapper authMapper;
  private final PlatformMapper platformMapper;
  private final UserMapper userMapper;
  private final FsdProperties fsdProperties;
  private final SystemBootstrapProperties systemBootstrapProperties;
  private final UserProfileStatsService userProfileStatsService;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    ensurePlatformConfig();
    ensureDefaultUser();
    ensureUserProfilesAndAuthTimestamps();
  }

  private void ensurePlatformConfig() {
    if (platformMapper.findFirstByOrderByIdAsc().isPresent()) {
      return;
    }

    platformMapper.save(
        Platform.builder()
            .platformName(trimToDefault(systemBootstrapProperties.getPlatformName(), "OpenAIMP"))
            .platformDescription(
                trimToDefault(
                    systemBootstrapProperties.getPlatformDescription(),
                    "Open Aerion Integrated Management Platform"))
            .platformLogo(trimToEmpty(systemBootstrapProperties.getPlatformLogo()))
            .platformUrl(trimToEmpty(systemBootstrapProperties.getPlatformUrl()))
            .platformSignedUserCount(
                trimToDefault(systemBootstrapProperties.getPlatformSignedUserCount(), "1"))
            .platformCreateTime(
                trimToDefault(
                    systemBootstrapProperties.getPlatformCreateTime(), LocalDate.now().toString()))
            .build());
  }

  private void ensureUserProfilesAndAuthTimestamps() {
    for (Auth auth : authMapper.findAllByOrderByCidAsc()) {
      boolean authChanged = false;
      if (auth.getRegisteredAt() == null) {
        auth.setRegisteredAt(LocalDateTime.now());
        authChanged = true;
      }
      if (authChanged) {
        authMapper.save(auth);
      }

      if (!userMapper.existsById(auth.getCid())) {
        userMapper.save(
            User.builder()
                .cid(auth.getCid())
                .nickname(resolveNickname(auth.getEmail(), auth.getCid()))
                .flightRecordCount(0)
                .flightDurationMinutes(0L)
                .controlRecordCount(0)
                .controlDurationMinutes(0L)
                .createdAt(auth.getRegisteredAt())
                .updatedAt(auth.getRegisteredAt())
                .build());
      } else {
        User profile = userMapper.findById(auth.getCid()).orElseThrow();
        boolean profileChanged = false;
        if (profile.getFlightRecordCount() == null) {
          profile.setFlightRecordCount(0);
          profileChanged = true;
        }
        if (profile.getFlightDurationMinutes() == null) {
          profile.setFlightDurationMinutes(0L);
          profileChanged = true;
        }
        if (profile.getControlRecordCount() == null) {
          profile.setControlRecordCount(0);
          profileChanged = true;
        }
        if (profile.getControlDurationMinutes() == null) {
          profile.setControlDurationMinutes(0L);
          profileChanged = true;
        }
        if (profileChanged) {
          profile.setUpdatedAt(LocalDateTime.now());
          userMapper.save(profile);
        }
      }

      userProfileStatsService.refreshStats(auth.getCid());
    }
  }

  private void ensureDefaultUser() {
    Integer cid = normalizeDefaultUserCid(systemBootstrapProperties.getDefaultUserCid());
    if (authMapper.existsById(cid)) {
      return;
    }

    String email =
        trimToDefault(systemBootstrapProperties.getDefaultUserEmail(), "admin@openaimp.local");
    if (authMapper.existsByEmail(email)) {
      throw new IllegalStateException("系统初始化默认用户失败，邮箱已被其他账号占用: " + email);
    }

    String rawPassword = resolveDefaultUserPassword();
    LocalDateTime now = LocalDateTime.now();
    int inserted = authMapper.insertWithCid(cid, email, SaSecureUtil.sha256(rawPassword), now, null);
    if (inserted != 1) {
      throw new IllegalStateException("系统初始化默认用户失败，未能写入 CID=" + cid + " 的账号");
    }
  }

  private Integer normalizeDefaultUserCid(Integer cid) {
    if (cid == null || cid <= 0) {
      throw new IllegalStateException("app.bootstrap.default-user-cid 必须是正整数");
    }
    return cid;
  }

  private String resolveDefaultUserPassword() {
    String configuredPassword = trimToEmpty(systemBootstrapProperties.getDefaultUserPassword());
    if (StringUtils.hasText(configuredPassword)) {
      return configuredPassword;
    }

    String fsdServicePassword = trimToEmpty(fsdProperties.getServicePassword());
    if (StringUtils.hasText(fsdServicePassword)) {
      return fsdServicePassword;
    }

    throw new IllegalStateException(
        "系统初始化默认用户失败，请配置 app.bootstrap.default-user-password 或 app.fsd.service-password");
  }

  private String trimToDefault(String value, String defaultValue) {
    String normalized = trimToEmpty(value);
    return StringUtils.hasText(normalized) ? normalized : defaultValue;
  }

  private String trimToEmpty(String value) {
    return value == null ? "" : value.trim();
  }

  private String resolveNickname(String email, Integer cid) {
    String normalized = trimToEmpty(email);
    if (StringUtils.hasText(normalized)) {
      int separatorIndex = normalized.indexOf('@');
      if (separatorIndex > 0) {
        return normalized.substring(0, separatorIndex).trim();
      }
      return normalized;
    }
    return "user" + cid;
  }
}
