package org.seckill.web.config;

import com.goodskill.common.ApiResult;
import com.goodskill.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


/**
 * 全局异常处理类
 *
 * @author techa03
 * @date 2020/12/22
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult exception(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(ResultCode.C500.getCode(), e.getMessage());
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult exception(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(errorInfo -> errorInfo.getField() + errorInfo.getDefaultMessage()).collect(Collectors.joining(","));
        log.warn(e.getMessage());
        return ApiResult.error(ResultCode.C500.getCode(), "参数校验失败:" + errorMsg);
    }

}
