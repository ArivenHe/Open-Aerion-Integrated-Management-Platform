package cn.ariven.openaimpbackend.controller;

import cn.ariven.openaimpbackend.common.Result;
import cn.ariven.openaimpbackend.dto.response.captcha.ResponseCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final StringRedisTemplate redisTemplate;
    private static final String CAPTCHA_PREFIX = "captcha:";

    @GetMapping("/image")
    public Result<ResponseCaptcha> getCaptcha() {
        // Generate line captcha (width, height, code count, line count)
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 150);
        
        String code = lineCaptcha.getCode();
        String key = UUID.randomUUID().toString();
        String imageBase64 = lineCaptcha.getImageBase64Data();

        // Store in Redis for 5 minutes
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + key, code, Duration.ofMinutes(5));

        return Result.success(new ResponseCaptcha(key, imageBase64));
    }
}
