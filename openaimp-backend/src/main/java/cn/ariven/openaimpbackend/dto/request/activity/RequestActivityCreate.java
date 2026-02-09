package cn.ariven.openaimpbackend.dto.request.activity;

import cn.ariven.openaimpbackend.enumeration.AirportRating;
import cn.ariven.openaimpbackend.enumeration.FlightDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestActivityCreate {
    @NotBlank(message = "需要活动时间")
    private String activityTime;

    @NotBlank(message = "需要起飞机场ICAO")
    private String depIcao;

    @NotBlank(message = "需要落地机场ICAO")
    private String appIcao;

    @NotNull(message = "需要机场等级")
    private AirportRating airportRating;

    private String notams;

    @NotNull(message = "需要飞行方向")
    private FlightDirection flightDirection;
}
