package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.request.RequestUpsertFlightRecord;
import cn.ariven.openaimpbackend.dto.response.ResponseFlightRecord;
import cn.ariven.openaimpbackend.mapper.AuthMapper;
import cn.ariven.openaimpbackend.mapper.FlightRecordMapper;
import cn.ariven.openaimpbackend.pojo.Auth;
import cn.ariven.openaimpbackend.pojo.FlightRecord;
import cn.ariven.openaimpbackend.service.FlightRecordService;
import cn.ariven.openaimpbackend.service.UserProfileStatsService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlightRecordServiceImpl implements FlightRecordService {
  private final FlightRecordMapper flightRecordMapper;
  private final AuthMapper authMapper;
  private final UserProfileStatsService userProfileStatsService;

  @Override
  public List<ResponseFlightRecord> listByUserCid(Integer userCid) {
    requirePositive(userCid, "userCid");
    userProfileStatsService.ensureProfile(userCid);
    return flightRecordMapper.findAllByUserCidOrderByStartedAtDescIdDesc(userCid).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ResponseFlightRecord getById(Long id) {
    return toResponse(getEntity(id));
  }

  @Override
  @Transactional
  public ResponseFlightRecord create(
      Integer userCid, RequestUpsertFlightRecord requestUpsertFlightRecord) {
    if (requestUpsertFlightRecord == null) {
      throw new IllegalArgumentException("飞行记录请求不能为空");
    }

    Integer normalizedUserCid = requirePositive(userCid, "userCid");
    userProfileStatsService.ensureProfile(normalizedUserCid);

    LocalDateTime startedAt = requireDateTime(requestUpsertFlightRecord.getStartedAt(), "startedAt");
    LocalDateTime endedAt = requireDateTime(requestUpsertFlightRecord.getEndedAt(), "endedAt");
    Long durationMinutes = resolveDurationMinutes(startedAt, endedAt, requestUpsertFlightRecord.getDurationMinutes());
    LocalDateTime now = LocalDateTime.now();

    FlightRecord saved =
        flightRecordMapper.save(
            FlightRecord.builder()
                .userCid(normalizedUserCid)
                .callsign(normalizeRequired(requestUpsertFlightRecord.getCallsign(), "callsign", 32))
                .departureAirport(normalizeNullable(requestUpsertFlightRecord.getDepartureAirport(), 8))
                .arrivalAirport(normalizeNullable(requestUpsertFlightRecord.getArrivalAirport(), 8))
                .startedAt(startedAt)
                .endedAt(endedAt)
                .durationMinutes(durationMinutes)
                .remarks(normalizeNullable(requestUpsertFlightRecord.getRemarks(), 1000))
                .createdAt(now)
                .updatedAt(now)
                .build());

    userProfileStatsService.refreshStats(normalizedUserCid);
    return toResponse(saved);
  }

  @Override
  @Transactional
  public ResponseFlightRecord update(Long id, RequestUpsertFlightRecord requestUpsertFlightRecord) {
    if (requestUpsertFlightRecord == null) {
      throw new IllegalArgumentException("飞行记录请求不能为空");
    }

    FlightRecord record = getEntity(id);
    LocalDateTime startedAt = requireDateTime(requestUpsertFlightRecord.getStartedAt(), "startedAt");
    LocalDateTime endedAt = requireDateTime(requestUpsertFlightRecord.getEndedAt(), "endedAt");
    Long durationMinutes = resolveDurationMinutes(startedAt, endedAt, requestUpsertFlightRecord.getDurationMinutes());

    record.setCallsign(normalizeRequired(requestUpsertFlightRecord.getCallsign(), "callsign", 32));
    record.setDepartureAirport(normalizeNullable(requestUpsertFlightRecord.getDepartureAirport(), 8));
    record.setArrivalAirport(normalizeNullable(requestUpsertFlightRecord.getArrivalAirport(), 8));
    record.setStartedAt(startedAt);
    record.setEndedAt(endedAt);
    record.setDurationMinutes(durationMinutes);
    record.setRemarks(normalizeNullable(requestUpsertFlightRecord.getRemarks(), 1000));
    record.setUpdatedAt(LocalDateTime.now());

    FlightRecord saved = flightRecordMapper.save(record);
    userProfileStatsService.refreshStats(saved.getUserCid());
    return toResponse(saved);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    FlightRecord record = getEntity(id);
    Integer userCid = record.getUserCid();
    flightRecordMapper.delete(record);
    userProfileStatsService.refreshStats(userCid);
  }

  private FlightRecord getEntity(Long id) {
    requirePositive(id, "id");
    return flightRecordMapper
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("飞行记录不存在: " + id));
  }

  private ResponseFlightRecord toResponse(FlightRecord record) {
    Auth auth =
        authMapper
            .findById(record.getUserCid())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + record.getUserCid()));
    return ResponseFlightRecord.builder()
        .id(record.getId())
        .userCid(record.getUserCid())
        .email(auth.getEmail())
        .callsign(record.getCallsign())
        .departureAirport(record.getDepartureAirport())
        .arrivalAirport(record.getArrivalAirport())
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
      throw new IllegalArgumentException("飞行时长必须大于 0 分钟");
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
}
