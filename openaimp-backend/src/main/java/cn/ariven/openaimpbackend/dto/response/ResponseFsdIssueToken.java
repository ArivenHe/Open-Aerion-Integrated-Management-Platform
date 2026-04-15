package cn.ariven.openaimpbackend.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFsdIssueToken {
  private String token;
  private String tokenType;
  private Integer cid;
  private Integer networkRating;
  private Instant expiresAt;
}
