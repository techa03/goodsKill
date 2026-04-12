package com.goodskill.ai.dto.response;

public record SourceDto(
        Long documentId,
        String documentTitle,
        Integer chunkIndex,
        Double score,
        String snippet
) {
}
