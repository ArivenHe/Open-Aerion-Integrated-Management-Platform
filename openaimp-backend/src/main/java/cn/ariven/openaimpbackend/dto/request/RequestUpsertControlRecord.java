package cn.ariven.openaimpbackend.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpsertControlRecord {
  private String position;
  private String airport;
  private Integer facilityType;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Long durationMinutes;
  private String remarks;
}
