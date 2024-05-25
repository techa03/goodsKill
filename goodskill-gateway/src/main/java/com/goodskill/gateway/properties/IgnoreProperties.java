package com.goodskill.gateway.properties;

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
     * 全局白名单配置
     */
    private List<String> whiteUrl = new ArrayList<>();

    /**
     * 登录用户访问白名单
     */
    private List<String> loginUserWhiteUrls = new ArrayList<>();

}
