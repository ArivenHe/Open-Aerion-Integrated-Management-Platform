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
public class ResponseFsdTokenClaims {
  private String issuer;
  private String tokenType;
  private Integer cid;
  private String firstName;
  private String lastName;
  private Integer networkRating;
  private Instant issuedAt;
  private Instant notBefore;
  private Instant expiresAt;
  private String jwtId;
}
