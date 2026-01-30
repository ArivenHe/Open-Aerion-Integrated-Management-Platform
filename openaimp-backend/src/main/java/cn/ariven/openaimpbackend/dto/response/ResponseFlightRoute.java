package cn.ariven.openaimpbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFlightRoute {
    private String startICAO;
    private String endICAO;
    private Double totalDistance;
    private String routeString;
    private List<RoutePoint> waypoints;
    private Map<String, Object> geoJson;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoutePoint {
        private String ident;
        private double lat;
        private double lon;
        private String type; // "Airport" or "Waypoint"
    }
}
