package com.goodskill.service.config;

import com.goodskill.core.info.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    /**
     * 测试：处理普通Exception时返回通用错误消息
     */
    @Test
    void shouldReturnGenericMessageForCheckedException() {
        Exception e = new Exception("some checked exception");

        Result<String> result = handler.handleException(e);

        assertNotNull(result);
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("系统异常，请稍后重试", result.getMsg());
    }

    /**
     * 测试：处理RuntimeException时返回异常自身的消息
     */
    @Test
    void shouldReturnExceptionMessageForRuntimeException() {
        RuntimeException e = new RuntimeException("业务规则校验失败");

        Result<String> result = handler.handleRuntimeException(e);

        assertNotNull(result);
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("业务规则校验失败", result.getMsg());
    }

    /**
     * 测试：处理message为null的RuntimeException时返回通用错误消息
     */
    @Test
    void shouldReturnGenericMessageWhenRuntimeExceptionMessageIsNull() {
        RuntimeException e = new RuntimeException((String) null);

        Result<String> result = handler.handleRuntimeException(e);

        assertNotNull(result);
        assertEquals(Result.FAIL, result.getCode());
        assertEquals("系统异常，请稍后重试", result.getMsg());
    }
}
