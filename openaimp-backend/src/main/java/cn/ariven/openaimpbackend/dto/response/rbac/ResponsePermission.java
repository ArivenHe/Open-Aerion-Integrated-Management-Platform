package cn.ariven.openaimpbackend.dto.response.rbac;

import lombok.Data;

@Data
public class ResponsePermission {
    private Long id;
    private String code;
    private String description;
}
