package com.goodskill.web.config;

import ch.qos.logback.classic.LoggerContext;
import com.goodskill.web.handler.Slf4jWebSocketLogHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogbackConfig implements ApplicationListener<ApplicationReadyEvent> {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    public void init() {
        log.info("\n=== LogbackConfig @PostConstruct ===");
        log.info("Spring配置初始化完成");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("\n=== ApplicationReadyEvent 触发 ===");
        log.info("应用程序完全启动，时间戳: {}", System.currentTimeMillis());

        // 动态添加Appender演示
        addDynamicAppender();
    }

    private void addDynamicAppender() {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            Slf4jWebSocketLogHandler dynamicAppender = new Slf4jWebSocketLogHandler();
            dynamicAppender.setMessagingTemplate(simpMessagingTemplate);
            dynamicAppender.setContext(loggerContext);
            dynamicAppender.start();

            loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
                .addAppender(dynamicAppender);

            log.info("动态Appender添加成功!");
        } catch (Exception e) {
            log.error("动态添加Appender失败: {}", e.getMessage());
        }
    }

}
