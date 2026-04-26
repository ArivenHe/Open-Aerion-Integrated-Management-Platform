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
public class ResponseFlightRecord {
  private Long id;
  private Integer userCid;
  private String email;
  private String callsign;
  private String departureAirport;
  private String arrivalAirport;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Long durationMinutes;
  private String remarks;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}
