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
public class RequestUpsertFlightRecord {
  private String callsign;
  private String departureAirport;
  private String arrivalAirport;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Long durationMinutes;
  private String remarks;
}
