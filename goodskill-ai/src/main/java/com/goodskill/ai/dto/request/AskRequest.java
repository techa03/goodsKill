package com.goodskill.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(
        @NotBlank(message = "问题不能为空") String question,
        Integer topK
) {
}
