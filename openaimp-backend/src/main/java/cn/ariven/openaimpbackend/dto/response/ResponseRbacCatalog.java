package cn.ariven.openaimpbackend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRbacCatalog {
  private List<RoleView> roles;
  private List<PermissionView> permissions;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoleView {
    private String code;
    private String name;
    private String description;
    private Boolean builtin;
    private List<String> permissions;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PermissionView {
    private String code;
    private String name;
    private String description;
    private Boolean builtin;
  }
}
