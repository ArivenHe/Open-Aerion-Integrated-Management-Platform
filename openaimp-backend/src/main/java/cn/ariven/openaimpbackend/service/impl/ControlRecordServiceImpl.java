package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.request.RequestUpsertControlRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseControlRecord;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.mapper.ControlRecordMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.ControlRecord;
import cn.ariven.openaimpbackend.service.ControlRecordService;
import cn.ariven.openaimpbackend.service.UserProfileStatsService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ControlRecordServiceImpl implements ControlRecordService {
  private final ControlRecordMapper controlRecordMapper;
  private final AuthMapper authMapper;
  private final UserProfileStatsService userProfileStatsService;

  @Override
  public List<ResponseControlRecord> listByUserCid(Integer userCid) {
    requirePositive(userCid, "userCid");
    userProfileStatsService.ensureProfile(userCid);
    return controlRecordMapper.findAllByUserCidOrderByStartedAtDescIdDesc(userCid).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ResponseControlRecord getById(Long id) {
    return toResponse(getEntity(id));
  }

  @Override
  @Transactional
  public ResponseControlRecord create(
      Integer userCid, RequestUpsertControlRecord requestUpsertControlRecord) {
    if (requestUpsertControlRecord == null) {
      throw new IllegalArgumentException("管制记录请求不能为空");
    }

    Integer normalizedUserCid = requirePositive(userCid, "userCid");
    userProfileStatsService.ensureProfile(normalizedUserCid);

    LocalDateTime startedAt = requireDateTime(requestUpsertControlRecord.getStartedAt(), "startedAt");
    LocalDateTime endedAt = requireDateTime(requestUpsertControlRecord.getEndedAt(), "endedAt");
    Long durationMinutes = resolveDurationMinutes(startedAt, endedAt, requestUpsertControlRecord.getDurationMinutes());
    LocalDateTime now = LocalDateTime.now();

    ControlRecord saved =
        controlRecordMapper.save(
            ControlRecord.builder()
                .userCid(normalizedUserCid)
                .position(normalizeRequired(requestUpsertControlRecord.getPosition(), "position", 32))
                .airport(normalizeNullable(requestUpsertControlRecord.getAirport(), 8))
                .facilityType(requireFacilityType(requestUpsertControlRecord.getFacilityType()))
                .startedAt(startedAt)
                .endedAt(endedAt)
                .durationMinutes(durationMinutes)
                .remarks(normalizeNullable(requestUpsertControlRecord.getRemarks(), 1000))
                .createdAt(now)
                .updatedAt(now)
                .build());

    userProfileStatsService.refreshStats(normalizedUserCid);
    return toResponse(saved);
  }

  @Override
  @Transactional
  public ResponseControlRecord update(
      Long id, RequestUpsertControlRecord requestUpsertControlRecord) {
    if (requestUpsertControlRecord == null) {
      throw new IllegalArgumentException("管制记录请求不能为空");
    }

    ControlRecord record = getEntity(id);
    LocalDateTime startedAt = requireDateTime(requestUpsertControlRecord.getStartedAt(), "startedAt");
    LocalDateTime endedAt = requireDateTime(requestUpsertControlRecord.getEndedAt(), "endedAt");
    Long durationMinutes = resolveDurationMinutes(startedAt, endedAt, requestUpsertControlRecord.getDurationMinutes());

    record.setPosition(normalizeRequired(requestUpsertControlRecord.getPosition(), "position", 32));
    record.setAirport(normalizeNullable(requestUpsertControlRecord.getAirport(), 8));
    record.setFacilityType(requireFacilityType(requestUpsertControlRecord.getFacilityType()));
    record.setStartedAt(startedAt);
    record.setEndedAt(endedAt);
    record.setDurationMinutes(durationMinutes);
    record.setRemarks(normalizeNullable(requestUpsertControlRecord.getRemarks(), 1000));
    record.setUpdatedAt(LocalDateTime.now());

    ControlRecord saved = controlRecordMapper.save(record);
    userProfileStatsService.refreshStats(saved.getUserCid());
    return toResponse(saved);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    ControlRecord record = getEntity(id);
    Integer userCid = record.getUserCid();
    controlRecordMapper.delete(record);
    userProfileStatsService.refreshStats(userCid);
  }

  private ControlRecord getEntity(Long id) {
    requirePositive(id, "id");
    return controlRecordMapper
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("管制记录不存在: " + id));
  }

  private ResponseControlRecord toResponse(ControlRecord record) {
    Auth auth =
        authMapper
            .findById(record.getUserCid())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + record.getUserCid()));
    return ResponseControlRecord.builder()
        .id(record.getId())
        .userCid(record.getUserCid())
        .email(auth.getEmail())
        .position(record.getPosition())
        .airport(record.getAirport())
        .facilityType(record.getFacilityType())
        .facilityTypeName(facilityTypeName(record.getFacilityType()))
        .startedAt(record.getStartedAt())
        .endedAt(record.getEndedAt())
        .durationMinutes(record.getDurationMinutes())
        .remarks(record.getRemarks())
        .createdAt(record.getCreatedAt())
        .updatedAt(record.getUpdatedAt())
        .build();
  }

  private Integer requirePositive(Integer value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " 必须为正整数");
    }
    return value;
  }

  private Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " 必须为正整数");
    }
    return value;
  }

  private Integer requireFacilityType(Integer facilityType) {
    if (facilityType == null || facilityType < 0 || facilityType > 6) {
      throw new IllegalArgumentException("facilityType 必须在 0 到 6 之间");
    }
    return facilityType;
  }

  private LocalDateTime requireDateTime(LocalDateTime value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " 不能为空");
    }
    return value;
  }

  private Long resolveDurationMinutes(
      LocalDateTime startedAt, LocalDateTime endedAt, Long durationMinutes) {
    if (!endedAt.isAfter(startedAt)) {
      throw new IllegalArgumentException("endedAt 必须晚于 startedAt");
    }
    long calculated = Duration.between(startedAt, endedAt).toMinutes();
    if (calculated <= 0) {
      throw new IllegalArgumentException("管制时长必须大于 0 分钟");
    }
    if (durationMinutes == null) {
      return calculated;
    }
    if (durationMinutes <= 0) {
      throw new IllegalArgumentException("durationMinutes 必须大于 0");
    }
    return durationMinutes;
  }

  private String normalizeRequired(String value, String fieldName, int maxLength) {
    String normalized = normalizeNullable(value, maxLength);
    if (normalized == null) {
      throw new IllegalArgumentException(fieldName + " 不能为空");
    }
    return normalized;
  }

  private String normalizeNullable(String value, int maxLength) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      return null;
    }
    if (normalized.length() > maxLength) {
      throw new IllegalArgumentException("字段长度不能超过 " + maxLength + " 个字符");
    }
    return normalized;
  }

  private String facilityTypeName(Integer facilityType) {
    return switch (facilityType) {
      case 0 -> "Observer";
      case 1 -> "FSS";
      case 2 -> "DEL";
      case 3 -> "GND";
      case 4 -> "TWR";
      case 5 -> "APP";
      case 6 -> "CTR";
      default -> "Unknown";
    };
  }
}
