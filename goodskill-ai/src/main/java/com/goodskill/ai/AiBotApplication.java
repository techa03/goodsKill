package com.goodskill.ai;

import com.goodskill.ai.config.KnowledgeBaseProperties;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 * AI 机器人启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.goodskill.ai.service.feign")
@EnableConfigurationProperties(KnowledgeBaseProperties.class)
public class AiBotApplication {

    @Bean
    public McpSyncHttpClientRequestCustomizer mcpSyncHttpClientRequestCustomizer() {
        return new McpSyncHttpClientRequestCustomizer() {
            @Override
            public void customize(HttpRequest.Builder builder, String method, URI endpoint, String body, McpTransportContext context) {
                // 从环境变量读取 Jina AI API Key，避免硬编码泄漏风险
                String jinaApiKey = System.getenv("JINA_API_KEY");
                if (jinaApiKey != null && !jinaApiKey.isEmpty()) {
                    builder.header("Authorization", "Bearer " + jinaApiKey);
                }
                builder.timeout(Duration.ofSeconds(120));
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(AiBotApplication.class, args);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

}
