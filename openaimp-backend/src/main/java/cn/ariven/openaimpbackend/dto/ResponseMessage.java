package cn.ariven.openaimpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    // 添加错误响应的静态方法
    public static <T> ResponseMessage<T> error(String message) {
        return new ResponseMessage<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }
    public static <T> ResponseMessage<T> Customize(Integer code, String message,T data) {
        return new ResponseMessage<>(code, message, data);
    }

    public static <T> ResponseMessage<T> error(Integer code, String message) {
        return new ResponseMessage<>(code, message, null);
    }

    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(HttpStatus.OK.value(), "success!", data);
    }
}
