package com.goodskill.gateway.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.stream.Collectors;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customizedLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("goodskill-order", r -> r.path("/api/order/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-order"))
                .route("goodskill-seata", r -> r.path("/api/seata/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-seata"))
                .route("goodskill-seckill", r -> r.path("/api/seckill/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-seckill"))
                .route("goodskill-auth", r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-auth"))
                .route("goodskill-web", r -> r.path("/api/web/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-web"))
                // WebSocket 路由规则，确保WebSocket握手请求正确处理
                .route("goodskill-web-websocket", r -> r.path("/api/web/ws/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-web"))
                .route("goodskill-common", r -> r.path("/api/common/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-common"))
                .build();
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
