package com.goodskill.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GatewayBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayBootApplication.class, args);
    }

}
