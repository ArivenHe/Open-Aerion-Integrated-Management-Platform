package cn.ariven.openaimpbackend.exception;

import cn.ariven.openaimpbackend.dto.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

  @ExceptionHandler(NotLoginException.class)
  public Result handleNotLoginException(NotLoginException e) {
    return new Result(401, "未登录或会话已过期", e.getMessage());
  }

  @ExceptionHandler(NotRoleException.class)
  public Result handleNotRoleException(NotRoleException e) {
    return new Result(403, "缺少所需角色", e.getMessage());
  }

  @ExceptionHandler(NotPermissionException.class)
  public Result handleNotPermissionException(NotPermissionException e) {
    return new Result(403, "缺少所需权限", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public Result handleIllegalArgumentException(IllegalArgumentException e) {
    return new Result(400, e.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public Result handleException(Exception e) {
    return new Result(500, e.getMessage(), null);
  }
}
