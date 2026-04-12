package com.goodskill.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 知识库配置属性
 * 默认值仅在配置文件未配置时生效
 */
@ConfigurationProperties(prefix = "goodskill.kb")
public class KnowledgeBaseProperties {

    /**
     * 默认检索返回数量
     */
    private int defaultTopK;

    public int getDefaultTopK() {
        return defaultTopK;
    }

    public void setDefaultTopK(int defaultTopK) {
        this.defaultTopK = defaultTopK;
    }
}
