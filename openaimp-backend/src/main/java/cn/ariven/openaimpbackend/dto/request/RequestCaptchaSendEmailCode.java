package cn.ariven.openaimpbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCaptchaSendEmailCode {
  private String email;
  private String captchaKey;
  private String imageCode;
}
