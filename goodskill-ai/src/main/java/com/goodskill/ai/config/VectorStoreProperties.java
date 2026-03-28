package com.goodskill.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vector Store properties for PostgreSQL pgvector
 * 默认值仅在配置文件未配置时生效
 */
@ConfigurationProperties(prefix = "goodskill.vectorstore")
public class VectorStoreProperties {

    /**
     * 向量表名
     */
    private String tableName;

    /**
     * 向量维度（适用于 bge-large-zh-v1.5 等模型）
     */
    private int dimensions;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
}
