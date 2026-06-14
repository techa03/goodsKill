package com.goodskill.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.goodskill.gateway", "com.goodskill.core.tracing"})
@EnableFeignClients
public class GatewayBootApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayBootApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }
}
