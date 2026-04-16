package cn.ariven.openaimpbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFsdUpdateUser {
  private Integer cid;
  private String password;
  private String firstName;
  private String lastName;
  private Integer networkRating;
}
