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
                .route("es-service-provider", r -> r.path("/api/es/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://es-service-provider"))
                .route("order-service-provider", r -> r.path("/api/mongo/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://order-service-provider"))
                .route("goodskill-seata", r -> r.path("/api/seata/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-seata"))
                .route("goodskill-service-provider", r -> r.path("/api/common/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-service-provider"))
                .route("goodskill-auth", r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://goodskill-auth"))
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
