package cn.ariven.openaimpbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFsdIssueToken {
  private String tokenType;
  private Integer cid;
  private String firstName;
  private String lastName;
  private Integer networkRating;
  private Long validitySeconds;
}
