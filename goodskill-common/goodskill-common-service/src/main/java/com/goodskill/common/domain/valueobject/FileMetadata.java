package com.goodskill.common.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileMetadata {
    private final String originalFileName;
    private final String contentType;
    private final long size;
    private final String fileMd5;
}