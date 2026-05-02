package com.goodskill.common.domain.aggregate;

import com.goodskill.common.domain.entity.FileNameMapping;
import com.goodskill.common.domain.valueobject.FileMetadata;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class FileAggregate {
    private final FileNameMapping fileNameMapping;
    private final FileMetadata fileMetadata;

    private FileAggregate(FileNameMapping fileNameMapping, FileMetadata fileMetadata) {
        this.fileNameMapping = fileNameMapping;
        this.fileMetadata = fileMetadata;
    }

    public static FileAggregate create(String originalFileName, String contentType, long size, String fileMd5) {
        FileNameMapping fileNameMapping = new FileNameMapping();
        fileNameMapping.setId(UUID.randomUUID().toString());
        fileNameMapping.setOriginalFileName(originalFileName);
        fileNameMapping.setUniqueFileName(UUID.randomUUID() + "_" + originalFileName);
        fileNameMapping.setFileMd5(fileMd5);
        fileNameMapping.setCreateTime(LocalDateTime.now());
        fileNameMapping.setUpdateTime(LocalDateTime.now());

        FileMetadata fileMetadata = new FileMetadata(originalFileName, contentType, size, fileMd5);

        return new FileAggregate(fileNameMapping, fileMetadata);
    }

    public static FileAggregate fromExisting(FileNameMapping fileNameMapping, FileMetadata fileMetadata) {
        return new FileAggregate(fileNameMapping, fileMetadata);
    }

    public void updateLastModified() {
        fileNameMapping.setUpdateTime(LocalDateTime.now());
    }
}