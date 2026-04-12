package com.goodskill.ai.dto.response;

import java.time.Instant;

public record KnowledgeDocumentDto(
        Long id,
        String title,
        Instant createdAt,
        long chunkCount
) {
}
