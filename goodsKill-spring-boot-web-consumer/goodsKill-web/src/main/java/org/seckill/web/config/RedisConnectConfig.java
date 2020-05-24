package org.seckill.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * redis配置，用于spring-session
 * @author techa03
 * @date 2020/5/24
 */
@Configuration
public class RedisConnectConfig {
    @Value("${redis.address}")
    private String redisAddress;
    @Value("${redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisAddress, redisPort));
    }
}
