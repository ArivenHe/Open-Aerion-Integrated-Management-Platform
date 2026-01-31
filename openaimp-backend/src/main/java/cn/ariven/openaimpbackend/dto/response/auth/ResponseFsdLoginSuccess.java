package cn.ariven.openaimpbackend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFsdLoginSuccess {
    private Boolean success;
    private String token;
}
