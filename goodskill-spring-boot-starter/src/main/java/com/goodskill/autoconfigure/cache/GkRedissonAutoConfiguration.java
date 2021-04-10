package com.goodskill.autoconfigure.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson分布式锁自动配置
 *
 * @author techa03
 * @since 2021/1/21
 */
@ConditionalOnClass(RedissonClient.class)
@Configuration(proxyBeanMethods = false)
@Deprecated(forRemoval = true)
public class GkRedissonAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.redis", name = {"host", "port"},
            matchIfMissing = true)
    @ConditionalOnMissingBean(name = "redissonClient")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String host = redisProperties.getHost().isBlank() ? "127.0.0.1" : redisProperties.getHost();
        int post = redisProperties.getPort() == 0 ? 6379 : redisProperties.getPort();
        config.useSingleServer().setAddress("redis://" + host + ":" + post);
        return Redisson.create(config);
    }
}
