package cn.ariven.openaimpbackend.exception;

import cn.ariven.openaimpbackend.common.Result;
import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    // 专门处理 SaToken 未登录异常
    @ExceptionHandler(NotLoginException.class)
    public Result handleNotLoginException(NotLoginException e) {
        return Result.failed(401, "未登录或会话已过期");
    }

    // 通用异常处理（保持原有逻辑）
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return Result.failed(500, e.getMessage());
    }
}