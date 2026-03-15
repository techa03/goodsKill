package com.goodskill.ai;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.stream.Collectors;

/**
 * AI机器人启动类
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.goodskill.ai.service.feign")
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
                builder.timeout(java.time.Duration.ofSeconds(120));
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(AiBotApplication.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * 修复使用feign client远程调用服务时报HttpMessageConverters缺失问题
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

}
