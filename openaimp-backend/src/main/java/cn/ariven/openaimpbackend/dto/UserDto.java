package cn.ariven.openaimpbackend.dto;

import lombok.Data;

@Data
public class UserDto {
    private String password;
    private String email;
    private String code;
    private String id;
    private String qq;
    private String callsign;

    public String getCid() {
        return id;
    }

    public void setCid(String id) {
        this.id = id;
    }
}
