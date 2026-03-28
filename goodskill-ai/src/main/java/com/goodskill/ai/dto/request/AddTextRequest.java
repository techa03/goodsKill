package com.goodskill.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddTextRequest(
        @NotBlank(message = "标题不能为空") String title,
        @NotBlank(message = "内容不能为空") String content
) {
}
