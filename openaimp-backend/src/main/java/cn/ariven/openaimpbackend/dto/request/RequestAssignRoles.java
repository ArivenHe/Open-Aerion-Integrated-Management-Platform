package cn.ariven.openaimpbackend.dto.request;

import java.util.List;
import lombok.Data;

@Data
public class RequestAssignRoles {
  private List<String> roleCodes;
}
