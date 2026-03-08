package com.goodskill.order.handler;

import com.goodskill.core.info.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("order service exception", e);
        return Result.fail("系统异常，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("order runtime exception", e);
        return Result.fail(e.getMessage() == null ? "系统异常，请稍后重试" : e.getMessage());
    }
}
