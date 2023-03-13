package com.goodskill.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 启动类
 *
 * @author heng
 */
@SpringBootApplication
@ImportResource(value = {"classpath*:META-INF/spring/spring-web.xml", "classpath*:META-INF/spring/spring-shiro-web.xml"})
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.goodskill.*.api", "com.goodskill.api"})
@EnableRedisHttpSession
@EnableAsync
public class SampleWebJspApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SampleWebJspApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }


}
