package com.goodskill.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 启动类
 *
 * @author heng
 */
@SpringBootApplication(scanBasePackages = {"com.goodskill.web",
        "com.goodskill.core.tracing",
        "com.goodskill.core.rest.client"})
@EnableDiscoveryClient
@EnableAsync
public class SampleWebApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SampleWebApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }


}
