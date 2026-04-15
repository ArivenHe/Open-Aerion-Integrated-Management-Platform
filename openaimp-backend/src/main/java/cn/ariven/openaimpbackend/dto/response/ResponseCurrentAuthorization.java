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
public class ResponseCurrentAuthorization {
  private Integer cid;
  private String email;
  private List<String> roles;
  private List<String> permissions;
}
