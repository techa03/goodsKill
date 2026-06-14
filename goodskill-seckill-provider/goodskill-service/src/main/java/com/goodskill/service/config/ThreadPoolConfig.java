package com.goodskill.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 * @author techa03
 * @date 2020/12/26
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * 业务异步执行器.
     * <p>使用 Spring 的 {@link ThreadPoolTaskExecutor} 以便显式应用 {@link ContextPropagatingTaskDecorator},
     * 跨线程调用时透传 trace context(MDC traceId/spanId + Observation),使分布式链路在异步任务中保持连续.
     * 参考: https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot
     */
    @Bean
    public TaskExecutor taskExecutor(ContextPropagatingTaskDecorator contextPropagatingTaskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds((int) TimeUnit.MINUTES.toSeconds(1));
        executor.setQueueCapacity(100);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(contextPropagatingTaskDecorator);
        executor.initialize();
        return executor;
    }
}
