package com.goodskill.oauth2.resourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * OAuth2.0 资源服务器启动类
 *
 * @author heng
 * @since 2021/9/18
 */
@SpringBootApplication
@EnableResourceServer
public class OAuth2ResourceServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApplication.class, args);
    }

}
