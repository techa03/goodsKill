package com.goodskill.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 启动类
 *
 * @author heng
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(value = {"com.goodskill.*.api", "com.goodskill.api"})
@EnableAsync
public class SampleWebApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SampleWebApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }


}
