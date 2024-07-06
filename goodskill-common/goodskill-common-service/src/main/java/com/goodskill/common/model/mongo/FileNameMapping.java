package com.goodskill.common.model.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document(collection = "oss_file_name_mapping")
@Data
public class FileNameMapping {
    @MongoId
    private String id;

    /**
     * 原始文件名
     */
    private String originalFileName;
    /**
     * 唯一文件名
     */
    private String uniqueFileName;
    /**
     * 文件 MD5 值
     */
    private String fileMd5;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
