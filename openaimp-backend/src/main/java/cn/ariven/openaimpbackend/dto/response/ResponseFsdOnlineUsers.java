package cn.ariven.openaimpbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFsdOnlineUsers {
  private List<PilotView> pilots;
  private List<AtcView> atc;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PilotView {
    private String callsign;
    private Integer cid;
    private String name;

    @JsonProperty("network_rating")
    private Integer networkRating;

    @JsonProperty("max_network_rating")
    private Integer maxNetworkRating;

    private Double latitude;
    private Double longitude;

    @JsonProperty("logon_time")
    private Instant logonTime;

    @JsonProperty("last_updated")
    private Instant lastUpdated;

    private Integer altitude;
    private Integer groundspeed;
    private Integer heading;
    private String transponder;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AtcView {
    private String callsign;
    private Integer cid;
    private String name;

    @JsonProperty("network_rating")
    private Integer networkRating;

    @JsonProperty("max_network_rating")
    private Integer maxNetworkRating;

    private Double latitude;
    private Double longitude;

    @JsonProperty("logon_time")
    private Instant logonTime;

    @JsonProperty("last_updated")
    private Instant lastUpdated;

    private String frequency;
    private Integer facility;

    @JsonProperty("visual_range")
    private Integer visualRange;
  }
}
