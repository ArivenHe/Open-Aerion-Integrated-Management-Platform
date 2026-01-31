package cn.ariven.openaimpbackend.dto.request.rbac;

import lombok.Data;

@Data
public class RequestPermissionUpdate {
    private Long id;
    private String code;
    private String description;
}
