package cn.ariven.openaimpbackend.exception;

import cn.ariven.openaimpbackend.dto.Result;
import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

  @ExceptionHandler(NotLoginException.class)
  public Result handleNotLoginException(NotLoginException e) {
    return new Result(401, "未登录或会话已过期", e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public Result handleException(Exception e) {
    return new Result(500, e.getMessage(), null);
  }
}
