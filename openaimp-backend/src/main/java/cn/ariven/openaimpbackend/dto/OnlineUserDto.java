package cn.ariven.openaimpbackend.dto;

import lombok.Data;

@Data
public class OnlineUserDto {
    private String type;
    private String callsign;
    private String cid;
    private String onlinetime;
    private String dep;
    private String arr;
    private String aircraft;
    private String conndate;
}
