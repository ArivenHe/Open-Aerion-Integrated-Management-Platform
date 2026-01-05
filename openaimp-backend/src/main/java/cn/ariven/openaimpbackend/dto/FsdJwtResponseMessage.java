package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FsdJwtResponseMessage<T> {
    private String token;
    private Boolean success;

    public FsdJwtResponseMessage(String token) {
        this.token = token;
        this.success = true;
    }
    public static <T> FsdJwtResponseMessage<T> success(String token) {
        return new FsdJwtResponseMessage<>(token);
    }
}
