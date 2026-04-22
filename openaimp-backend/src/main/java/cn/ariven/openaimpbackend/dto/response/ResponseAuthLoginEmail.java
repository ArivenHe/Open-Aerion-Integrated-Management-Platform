package cn.ariven.openaimpbackend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAuthLoginEmail {
  private Integer cid;
  private String token;
  private UserInfo user;
  private RbacInfo rbac;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserInfo {
    private Integer cid;
    private String email;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RbacInfo {
    private List<String> roles;
    private List<String> permissions;
  }
}
