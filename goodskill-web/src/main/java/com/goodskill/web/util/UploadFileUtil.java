package com.goodskill.web.util;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Component
@Slf4j
public class UploadFileUtil {
    @Autowired
    private MinioClient minioClient;
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucketName}")
    private String bucketName;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName).object(file.getOriginalFilename())
                .stream(file.getInputStream(), file.getSize(), 5 * 1024 * 1024).build());
        log.info(String.valueOf(objectWriteResponse));
        return endpoint + "/" + bucketName + "/" + URLEncoder.encode(file.getOriginalFilename(), Charset.defaultCharset());
    }

    private UploadFileUtil() {
    }

}
