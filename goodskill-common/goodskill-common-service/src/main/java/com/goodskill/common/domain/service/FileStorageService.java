package com.goodskill.common.domain.service;

import java.io.InputStream;

public interface FileStorageService {
    void uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType);

    InputStream downloadFile(String bucketName, String objectName);

    String generatePresignedUrl(String bucketName, String objectName, int expirySeconds);

    String getPublicUrl(String endpoint, String bucketName, String objectName);
}