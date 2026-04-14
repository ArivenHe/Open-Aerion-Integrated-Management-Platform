package cn.ariven.openaimpbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCaptchaSendEmailCode {
  private String email;
  private Long expireSeconds;
}
