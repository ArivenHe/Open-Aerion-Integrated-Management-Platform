package cn.ariven.openaimpbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
    private Integer rating;
    private String nickname;
    private String firstName;
    private String lastName;
    private String avatar;
    private String bio;
    private Integer flightRecordCount;
    private Long flightDurationMinutes;
    private Integer controlRecordCount;
    private Long controlDurationMinutes;

    @JsonProperty("registered_at")
    private LocalDateTime registeredAt;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    @JsonProperty("net_rating")
    private Integer netRating;
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
