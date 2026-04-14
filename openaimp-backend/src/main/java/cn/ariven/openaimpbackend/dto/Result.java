package cn.ariven.openaimpbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {
  private int code;
  private String message;
  private T data;

  public Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public Result(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> Result<T> success(T data) {
    return new Result<>(200, "success", data);
  }

  public static <T> Result<T> success() {
    return new Result<>(200, "success");
  }

  public static <T> Result<T> success(String message, T data) {
    return new Result<>(200, message, data);
  }

  public static <T> Result<T> fail(int code, String message) {
    return new Result<>(code, message);
  }

  public static <T> Result<T> fail(int code, String message, T data) {
    return new Result<>(code, message, data);
  }

  public static <T> Result<T> fail(String message) {
    return new Result<>(400, message);
  }

  public static <T> Result<T> fail(String message, T data) {
    return new Result<>(400, message, data);
  }
}
