package cn.ariven.openaimpbackend.dto.request.activity;

import cn.ariven.openaimpbackend.enumeration.AirportRating;
import cn.ariven.openaimpbackend.enumeration.FlightDirection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestActivityUpdate {
    @NotNull(message = "需要活动ID")
    private Long id;

    private String activityTime;

    private String depIcao;

    private String appIcao;

    private AirportRating airportRating;

    private String notams;

    private FlightDirection flightDirection;
}
