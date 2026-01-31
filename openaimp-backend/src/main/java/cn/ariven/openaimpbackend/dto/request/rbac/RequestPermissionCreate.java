package cn.ariven.openaimpbackend.dto.request.rbac;

import lombok.Data;

@Data
public class RequestPermissionCreate {
    private String code;
    private String description;
}
