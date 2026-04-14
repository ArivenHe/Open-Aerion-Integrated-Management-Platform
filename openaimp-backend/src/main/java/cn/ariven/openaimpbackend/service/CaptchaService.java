package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestCaptchaSendEmailCode;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaImage;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaSendEmailCode;

public interface CaptchaService {
  Result<ResponseCaptchaImage> getImageCaptcha();

  Result<ResponseCaptchaSendEmailCode> sendEmailCode(
      RequestCaptchaSendEmailCode requestCaptchaSendEmailCode);

  boolean validateEmailCode(String email, String emailCode);
}
