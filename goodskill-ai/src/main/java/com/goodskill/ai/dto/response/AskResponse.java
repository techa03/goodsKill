package com.goodskill.ai.dto.response;

import java.util.List;

public record AskResponse(
        String answer,
        List<SourceDto> sources
) {
}
