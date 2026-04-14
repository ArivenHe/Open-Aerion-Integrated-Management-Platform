package cn.ariven.openaimpbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCaptchaImage {
  private String captchaKey;
  private String imageBase64;
  private Long expireSeconds;
}
