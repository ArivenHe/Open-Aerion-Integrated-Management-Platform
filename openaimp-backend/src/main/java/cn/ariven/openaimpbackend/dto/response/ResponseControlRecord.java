package cn.ariven.openaimpbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseControlRecord {
  private Long id;
  private Integer userCid;
  private String email;
  private String position;
  private String airport;
  private Integer facilityType;
  private String facilityTypeName;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Long durationMinutes;
  private String remarks;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}
