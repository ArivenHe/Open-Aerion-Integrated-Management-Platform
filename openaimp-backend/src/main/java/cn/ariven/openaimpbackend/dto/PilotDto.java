package cn.ariven.openaimpbackend.dto;

import lombok.Data;

@Data
public class PilotDto {
    private String cid;
    private String callsign;
    private String onlinetime;
    private String dep;
    private String arr;
    private String aircraft;
    private String conndate;
}
