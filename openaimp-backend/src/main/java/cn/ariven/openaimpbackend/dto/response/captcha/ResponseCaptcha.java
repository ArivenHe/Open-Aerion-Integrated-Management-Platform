package cn.ariven.openaimpbackend.dto.response.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCaptcha {
    private String key;
    private String imageBase64;
}
