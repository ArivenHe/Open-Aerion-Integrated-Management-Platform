package cn.ariven.openaimpbackend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMetar {
    private String icao;        // 机场代码
    private String airportName; // 机场名称
    private String time;        // 观测时间
    private Integer windDir;    // 风向 (度)
    private Integer windSpeed;  // 风速
    private String windUnit;    // 单位 (MPS/KT)
    private String visibility;  // 能见度
    private String visibilityUnit;//能见度单位
    private Integer temperature;// 气温
    private Integer dewPoint;   // 露点
    private Integer qnh;        // 修正海压
    private String qnhUnit; //修正海亚单位
}
