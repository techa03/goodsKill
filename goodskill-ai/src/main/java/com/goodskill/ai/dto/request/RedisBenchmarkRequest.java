package com.goodskill.ai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Redis 压测请求参数。
 */
public record RedisBenchmarkRequest(
        @NotNull(message = "mode 不能为空")
        RedisBenchmarkMode mode,

        @Min(value = 1, message = "iterations 最小为 1")
        @Max(value = 200000, message = "iterations 最大为 200000")
        Integer iterations,

        @NotBlank(message = "key 不能为空")
        String key,

        @Min(value = 1, message = "ttlSeconds 最小为 1")
        @Max(value = 86400, message = "ttlSeconds 最大为 86400")
        Integer ttlSeconds,

        @Min(value = 1, message = "valueSize 最小为 1")
        @Max(value = 32768, message = "valueSize 最大为 32768")
        Integer valueSize
) {
    public enum RedisBenchmarkMode {
        PING,
        INCR,
        SET_GET
    }
}
