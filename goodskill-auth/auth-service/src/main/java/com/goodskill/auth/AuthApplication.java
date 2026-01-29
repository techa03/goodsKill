package com.goodskill.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {
        "com.goodskill.auth",
        "com.goodskill.core",
})
@MapperScan("com.goodskill.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }

}
