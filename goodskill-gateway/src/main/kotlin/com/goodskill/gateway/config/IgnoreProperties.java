package com.goodskill.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单配置
 *
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreProperties
{
    /**
     * 白名单配置
     */
    private List<String> whiteUrl = new ArrayList<>();

    public List<String> getWhiteUrl() {
        return whiteUrl;
    }

    public void setWhiteUrl(List<String> whiteUrl) {
        this.whiteUrl = whiteUrl;
    }
}
