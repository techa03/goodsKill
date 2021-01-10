package org.seckill.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置默认的响应格式
 * @author techa03
 */
@Configuration
public class ConfigInterceptor implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // 这里一定要设置为true，才能覆盖原来的默认的xml format
        configurer.ignoreAcceptHeader(true)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }
}