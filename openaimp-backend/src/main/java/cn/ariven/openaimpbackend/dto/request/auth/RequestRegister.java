package cn.ariven.openaimpbackend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestRegister {
    @NotBlank(message = "Callsign is required")
    private String callsign;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String qq;

    @NotBlank(message = "Captcha key is required")
    private String captchaKey;

    @NotBlank(message = "Captcha code is required")
    private String captchaCode;
}
