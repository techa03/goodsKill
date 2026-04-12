package com.goodskill.web.handler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * SLF4J日志WebSocket处理器
 *
 * @author heng
 */
public class Slf4jWebSocketLogHandler extends AppenderBase<ILoggingEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Slf4jWebSocketLogHandler.class);

    private SimpMessagingTemplate messagingTemplate;

    // 缓存队列，用于存储messagingTemplate初始化前的日志
    private static final ConcurrentLinkedQueue<LogMessage> LOG_CACHE = new ConcurrentLinkedQueue<>();
    // 缓存大小限制，防止内存溢出
    private static final int MAX_CACHE_SIZE = 1000;

    private final static String LOG_TOPIC = "/topic/logs";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        if (messagingTemplate == null) {
            logger.error("尝试注入null的messagingTemplate");
            return;
        }

        this.messagingTemplate = messagingTemplate;
        logger.debug("SimpMessagingTemplate 已成功注入，对象信息: {}", messagingTemplate.toString());
        logger.info("SimpMessagingTemplate 已注入到 Slf4jWebSocketLogHandler");

        // 验证注入是否成功
        if (this.messagingTemplate != null) {
            logger.debug("messagingTemplate注入验证成功");
        } else {
            logger.error("messagingTemplate注入验证失败，仍然为null");
        }

        // 如果有缓存的日志，发送出去
        processCache();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        // 过滤低级别日志
        if (eventObject.getLevel().toInt() < ch.qos.logback.classic.Level.INFO.toInt()) {
            return;
        }

        // 过滤非业务logger
        String loggerName = eventObject.getLoggerName();
        if (!loggerName.startsWith("com.goodskill")) {
            return;
        }

        // 格式化日志消息
        String timestamp = dateFormat.format(new Date(eventObject.getTimeStamp()));
        String level = eventObject.getLevel().levelStr;
        String message = eventObject.getFormattedMessage();

        // 构建日志对象
        LogMessage logMessage = new LogMessage();
        logMessage.setTimestamp(timestamp);
        logMessage.setLevel(level);
        logMessage.setLogger(loggerName);
        logMessage.setMessage(message);

        // 发送到WebSocket客户端
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend(LOG_TOPIC, logMessage);
                logger.debug("成功发送日志到WebSocket: {}", message);
            } catch (Exception e) {
                logger.error("发送日志到WebSocket失败: ", e);
            }
        } else {
            // 如果template还没初始化，存入缓存
            if (LOG_CACHE.size() < MAX_CACHE_SIZE) {
                LOG_CACHE.offer(logMessage);
                logger.debug("messagingTemplate为null，日志已缓存，当前缓存大小: {}", LOG_CACHE.size());
            } else {
                logger.warn("日志缓存已满，丢弃新日志: {}", message);
            }
        }
    }

    private void processCache() {
        if (messagingTemplate == null) {
            return;
        }

        LogMessage logMessage;
        while ((logMessage = LOG_CACHE.poll()) != null) {
            // 过滤无效日志
            if (logMessage.getLevel() == null) {
                continue;
            }
            
            // 过滤低级别日志
            ch.qos.logback.classic.Level level = ch.qos.logback.classic.Level.toLevel(logMessage.getLevel());
            if (level.toInt() < ch.qos.logback.classic.Level.INFO.toInt()) {
                continue;
            }
            
            // 过滤非业务logger
            if (logMessage.getLogger() == null || !logMessage.getLogger().startsWith("com.goodskill")) {
                continue;
            }
            
            try {
                messagingTemplate.convertAndSend(LOG_TOPIC, logMessage);
            } catch (Exception e) {
                logger.error("发送缓存日志失败: ", e);
            }
        }
    }


    /**
     * 日志消息对象
     */
    public static class LogMessage {
        private String timestamp;
        private String level;
        private String logger;
        private String message;

        // Getters and Setters
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getLogger() {
            return logger;
        }

        public void setLogger(String logger) {
            this.logger = logger;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
