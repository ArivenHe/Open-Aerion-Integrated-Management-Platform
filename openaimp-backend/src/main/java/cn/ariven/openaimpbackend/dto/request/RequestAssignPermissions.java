package cn.ariven.openaimpbackend.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class RequestAssignPermissions {
  private List<String> permissionCodes;
}
