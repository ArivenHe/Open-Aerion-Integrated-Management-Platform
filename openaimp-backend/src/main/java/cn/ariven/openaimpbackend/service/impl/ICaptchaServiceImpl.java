package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.Result;
import cn.ariven.openaimpbackend.dto.request.RequestCaptchaSendEmailCode;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaImage;
import cn.ariven.openaimpbackend.dto.response.ResponseCaptchaSendEmailCode;
import cn.ariven.openaimpbackend.service.CaptchaService;
import cn.ariven.openaimpbackend.utils.VerifyUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ICaptchaServiceImpl implements CaptchaService {

  private static final long IMAGE_CAPTCHA_EXPIRE_SECONDS = 300L;
  private static final long EMAIL_CODE_EXPIRE_SECONDS = 300L;

  private final StringRedisTemplate stringRedisTemplate;
  private final JavaMailSender javaMailSender;

  @Value("${app.mail.from:}")
  private String mailFrom;

  @Override
  public Result<ResponseCaptchaImage> getImageCaptcha() {
    Object[] captcha =
        VerifyUtil.newBuilder()
            .setWidth(120)
            .setHeight(35)
            .setSize(4)
            .setLines(10)
            .setFontSize(25)
            .setTilt(true)
            .setBackgroundColor(Color.LIGHT_GRAY)
            .build()
            .createImage();

    String captchaCode = ((String) captcha[0]).toLowerCase(Locale.ROOT);
    BufferedImage captchaImage = (BufferedImage) captcha[1];
    String captchaKey = UUID.randomUUID().toString().replace("-", "");

    stringRedisTemplate
        .opsForValue()
        .set(buildImageCaptchaKey(captchaKey), captchaCode, IMAGE_CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

    ResponseCaptchaImage responseCaptchaImage =
        ResponseCaptchaImage.builder()
            .captchaKey(captchaKey)
            .imageBase64("data:image/png;base64," + toBase64(captchaImage))
            .expireSeconds(IMAGE_CAPTCHA_EXPIRE_SECONDS)
            .build();

    return Result.success("图形验证码获取成功", responseCaptchaImage);
  }

  @Override
  public Result<ResponseCaptchaSendEmailCode> sendEmailCode(
      RequestCaptchaSendEmailCode requestCaptchaSendEmailCode) {
    if (requestCaptchaSendEmailCode == null
        || isBlank(requestCaptchaSendEmailCode.getEmail())
        || isBlank(requestCaptchaSendEmailCode.getCaptchaKey())
        || isBlank(requestCaptchaSendEmailCode.getImageCode())) {
      return Result.fail("邮箱、图片验证码标识和图片验证码不能为空");
    }

    String cacheKey = buildImageCaptchaKey(requestCaptchaSendEmailCode.getCaptchaKey());
    String cachedImageCode = stringRedisTemplate.opsForValue().get(cacheKey);
    if (cachedImageCode == null) {
      return Result.fail("图片验证码已过期,请重新获取");
    }

    if (!cachedImageCode.equalsIgnoreCase(requestCaptchaSendEmailCode.getImageCode().trim())) {
      return Result.fail("图片验证码错误");
    }

    stringRedisTemplate.delete(cacheKey);

    String emailCode = generateEmailCode();
    stringRedisTemplate
        .opsForValue()
        .set(
            buildEmailCaptchaKey(requestCaptchaSendEmailCode.getEmail()),
            emailCode,
            EMAIL_CODE_EXPIRE_SECONDS,
            TimeUnit.SECONDS);

    try {
      sendEmail(
          requestCaptchaSendEmailCode.getEmail(),
          "OpenAIMP 邮箱验证码",
          buildEmailContent(emailCode));
    } catch (MailException | IllegalStateException e) {
      stringRedisTemplate.delete(buildEmailCaptchaKey(requestCaptchaSendEmailCode.getEmail()));
      return Result.fail("邮箱验证码发送失败,请检查邮件配置后重试");
    }

    ResponseCaptchaSendEmailCode responseCaptchaSendEmailCode =
        ResponseCaptchaSendEmailCode.builder()
            .email(requestCaptchaSendEmailCode.getEmail())
            .expireSeconds(EMAIL_CODE_EXPIRE_SECONDS)
            .build();

    return Result.success("邮箱验证码发送成功", responseCaptchaSendEmailCode);
  }

  @Override
  public boolean validateEmailCode(String email, String emailCode) {
    if (isBlank(email) || isBlank(emailCode)) {
      return false;
    }

    String cacheKey = buildEmailCaptchaKey(email);
    String cachedEmailCode = stringRedisTemplate.opsForValue().get(cacheKey);
    if (cachedEmailCode == null || !cachedEmailCode.equals(emailCode.trim())) {
      return false;
    }

    stringRedisTemplate.delete(cacheKey);
    return true;
  }

  private String buildImageCaptchaKey(String captchaKey) {
    return "captcha:image:" + captchaKey;
  }

  private String buildEmailCaptchaKey(String email) {
    return "captcha:email:" + email;
  }

  private String generateEmailCode() {
    return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
  }

  private String toBase64(BufferedImage captchaImage) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(captchaImage, "png", outputStream);
      return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("图形验证码生成失败", e);
    }
  }

  private void sendEmail(String to, String subject, String content) {
    if (isBlank(mailFrom)) {
      throw new IllegalStateException("未配置发件人邮箱");
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(mailFrom);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(content);
    javaMailSender.send(message);
  }

  private String buildEmailContent(String emailCode) {
    return "您好，您的 OpenAIMP 邮箱验证码为："
        + emailCode
        + "，5分钟内有效。若非本人操作，请忽略此邮件。";
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
