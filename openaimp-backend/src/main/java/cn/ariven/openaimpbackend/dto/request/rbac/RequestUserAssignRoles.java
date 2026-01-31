package cn.ariven.openaimpbackend.dto.request.rbac;

import lombok.Data;
import java.util.List;

@Data
public class RequestUserAssignRoles {
    private Long userId;
    private List<Long> roleIds;
}
