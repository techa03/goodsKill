package org.seckill.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 * @author techa03
 * @date 2020/12/26
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor taskExecutor() {
        return new ThreadPoolExecutor(2, 10, 1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
