package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestCaptchaSendEmailCode;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaImage;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaSendEmailCode;
import cn.ariven.openaimpbackend.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

  private final CaptchaService captchaService;

  @GetMapping("/image")
  public Result<ResponseCaptchaImage> getImageCaptcha() {
    return captchaService.getImageCaptcha();
  }

  @PostMapping("/email/send")
  public Result<ResponseCaptchaSendEmailCode> sendEmailCode(
      @RequestBody RequestCaptchaSendEmailCode requestCaptchaSendEmailCode) {
    return captchaService.sendEmailCode(requestCaptchaSendEmailCode);
  }
}
