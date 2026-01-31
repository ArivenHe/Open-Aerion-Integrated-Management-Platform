package cn.ariven.openaimpbackend.dto.request.rbac;

import lombok.Data;
import java.util.List;

@Data
public class RequestRoleAssignPermissions {
    private Long roleId;
    private List<String> permissionCodes;
}
