package cn.ariven.openaimpbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Callsign or Email is required")
    private String account; // Can be callsign or email

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Captcha key is required")
    private String captchaKey;

    @NotBlank(message = "Captcha code is required")
    private String captchaCode;
}
