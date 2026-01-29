package cn.ariven.openaimpbackend.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VOAtisInfo {
    private String icao;
    private String airportName;
    private String atisCode;
    private String timeUtc;

    private String windDirection;
    private int windSpeed;
    private String windUnit;

    private boolean isCavok;
    private String visibility;
    private List<CloudInfo> clouds;

    private int temperature;
    private int dewPoint;
    private int qnh;
    @Builder.Default
    private String qnhUnit = "hPa";

    private String atisFullCn;
    private String atisFullEn;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CloudInfo {
        private String quantity; // FEW, SCT, etc.
        private int height;
        private String type;     // CB, TCU
    }
}
