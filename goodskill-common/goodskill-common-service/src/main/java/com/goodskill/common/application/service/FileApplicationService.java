package com.goodskill.common.application.service;

import com.goodskill.common.domain.aggregate.FileAggregate;
import com.goodskill.common.domain.entity.FileNameMapping;
import com.goodskill.common.domain.repository.FileNameMappingRepository;
import com.goodskill.common.domain.service.FileStorageService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@Service
public class FileApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(FileApplicationService.class);

    private final FileStorageService fileStorageService;
    private final FileNameMappingRepository fileNameMappingRepository;
    private final String bucketName;
    private final String minioEndpoint;

    public FileApplicationService(FileStorageService fileStorageService,
                                 FileNameMappingRepository fileNameMappingRepository,
                                 @org.springframework.beans.factory.annotation.Value("${minio.bucketName}") String bucketName,
                                 @org.springframework.beans.factory.annotation.Value("${minio.endpoint}") String minioEndpoint) {
        this.fileStorageService = fileStorageService;
        this.fileNameMappingRepository = fileNameMappingRepository;
        this.bucketName = bucketName;
        this.minioEndpoint = minioEndpoint;
    }

    public String uploadFile(MultipartFile file) {
        try {
            // 计算文件的 MD5 哈希值
            String fileMd5 = DigestUtils.md5Hex(file.getInputStream());

            // 检查文件是否已经存在
            Optional<FileNameMapping> existingMapping = fileNameMappingRepository.findFirstByFileMd5(fileMd5);
            String uniqueFileName;

            if (existingMapping.isPresent()) {
                uniqueFileName = existingMapping.get().getUniqueFileName();
                logger.info("File already exists: {}", file.getOriginalFilename());
            } else {
                // 创建文件聚合根
                FileAggregate fileAggregate = FileAggregate.create(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        fileMd5
                );
                uniqueFileName = fileAggregate.getFileNameMapping().getUniqueFileName();

                // 上传文件到存储服务
                fileStorageService.uploadFile(
                        bucketName,
                        uniqueFileName,
                        file.getInputStream(),
                        file.getSize(),
                        file.getContentType()
                );

                // 保存文件映射关系
                fileNameMappingRepository.save(fileAggregate.getFileNameMapping());
            }

            // 构建公开访问链接
            String publicUrl = fileStorageService.getPublicUrl(minioEndpoint, bucketName, uniqueFileName);

            logger.info("文件上传成功: {}, 公开链接: {}", uniqueFileName, publicUrl);
            return publicUrl;
        } catch (Exception e) {
            logger.error("文件上传失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    public Object downloadFile(String fileId, String responseType) {
        try {
            // 从仓储获取文件映射
            Optional<FileNameMapping> fileNameMapping = fileNameMappingRepository.findById(fileId);
            if (fileNameMapping.isEmpty()) {
                logger.warn("File not found: {}", fileId);
                throw new RuntimeException("File not found");
            }

            if ("binary".equalsIgnoreCase(responseType)) {
                // 返回二进制数据
                InputStream inputStream = fileStorageService.downloadFile(
                        bucketName,
                        fileNameMapping.get().getUniqueFileName()
                );

                byte[] content = inputStream.readAllBytes();
                inputStream.close();

                logger.info("File downloaded as binary: {}", fileNameMapping.get().getOriginalFileName());
                return content;
            } else {
                // 返回文件的 URL
                String presignedObjectUrl = fileStorageService.generatePresignedUrl(
                        bucketName,
                        fileNameMapping.get().getUniqueFileName(),
                        60 * 60 * 24
                );
                logger.info("File URL generated: {}", presignedObjectUrl);
                return presignedObjectUrl;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("文件下载失败: {}", fileId, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }
}
