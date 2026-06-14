package com.goodskill.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.goodskill.gateway", "com.goodskill.core.tracing"})
public class GatewayBootApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayBootApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }
}