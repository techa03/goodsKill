package com.goodskill.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth.token")
public class AuthTokenProperties {
    /**
     * refresh token 过期时间，默认7天（秒）
     */
    private long refreshExpireSeconds = 604800;
}
