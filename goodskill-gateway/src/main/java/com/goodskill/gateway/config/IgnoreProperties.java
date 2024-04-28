package com.goodskill.gateway.config;

import lombok.Data;
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
@Data
public class IgnoreProperties
{
    /**
     * 白名单配置
     */
    private List<String> whiteUrl = new ArrayList<>();

}
