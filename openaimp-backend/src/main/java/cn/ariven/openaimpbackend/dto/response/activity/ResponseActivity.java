package cn.ariven.openaimpbackend.dto.response.activity;

import cn.ariven.openaimpbackend.enumeration.AirportRating;
import cn.ariven.openaimpbackend.enumeration.FlightDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseActivity {
    private Long id;
    private String activityTime;
    private String depIcao;
    private String appIcao;
    private AirportRating airportRating;
    private String notams;
    private FlightDirection flightDirection;
    private List<ResponseActivityUser> pilotList;
    private List<ResponseActivityUser> atcList;
}
