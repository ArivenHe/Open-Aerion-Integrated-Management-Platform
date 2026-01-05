package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FsdJwtResponseFailedMessage<T> {
    private String error_msg;
    private Boolean success;

    public FsdJwtResponseFailedMessage(String message) {
        this.error_msg = message;
        this.success = false;
    }
    public static <T> FsdJwtResponseFailedMessage<T> success(String message) {
        return new FsdJwtResponseFailedMessage<>(message);
    }
}
