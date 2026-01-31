package cn.ariven.openaimpbackend.dto.request.rbac;

import lombok.Data;

@Data
public class RequestRoleCreate {
    private String name;
    private String description;
}
