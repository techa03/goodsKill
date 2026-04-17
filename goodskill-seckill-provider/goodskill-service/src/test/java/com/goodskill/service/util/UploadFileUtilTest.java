package com.goodskill.service.util;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UploadFileUtil 单元测试
 */
@ExtendWith(MockitoExtension.class)
class UploadFileUtilTest {

    @InjectMocks
    private UploadFileUtil uploadFileUtil;

    @Mock
    private MinioClient minioClient;

    @Test
    void shouldUploadFileSuccessfully() throws Exception {
        // Given
        ReflectionTestUtils.setField(uploadFileUtil, "endpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(uploadFileUtil, "bucketName", "test-bucket");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test content".getBytes()
        );

        ObjectWriteResponse response = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(response);

        // When
        String result = uploadFileUtil.uploadFile(file);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:9000"));
        assertTrue(result.contains("test-bucket"));
        assertTrue(result.contains("test-image.jpg"));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldUploadFileWithChineseFilename() throws Exception {
        // Given
        ReflectionTestUtils.setField(uploadFileUtil, "endpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(uploadFileUtil, "bucketName", "test-bucket");

        MockMultipartFile file = new MockMultipartFile(
                "file", "测试图片.jpg", "image/jpeg", "test content".getBytes()
        );

        ObjectWriteResponse response = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(response);

        // When
        String result = uploadFileUtil.uploadFile(file);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:9000/test-bucket/"));
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }
}
