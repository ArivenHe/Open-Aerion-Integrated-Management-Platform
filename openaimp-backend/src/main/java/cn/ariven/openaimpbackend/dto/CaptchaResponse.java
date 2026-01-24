package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String key;
    private String imageBase64;
}
