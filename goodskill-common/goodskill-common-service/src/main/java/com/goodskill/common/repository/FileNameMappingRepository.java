package com.goodskill.common.repository;

import com.goodskill.common.model.mongo.FileNameMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileNameMappingRepository extends MongoRepository<FileNameMapping, String> {
    Optional<FileNameMapping>  findByOriginalFileName(String originalFileName);

    Optional<FileNameMapping> findByUniqueFileName(String uniqueFileName);

    Optional<FileNameMapping> findFirstByFileMd5(String fileMd5);
}
