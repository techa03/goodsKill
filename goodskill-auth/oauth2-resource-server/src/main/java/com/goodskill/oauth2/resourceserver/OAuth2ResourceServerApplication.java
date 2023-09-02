package com.goodskill.oauth2.resourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OAuth2.0资源服务器启动类
 *
 * @author techa03
 * @since 2022/1/28
 */
@SpringBootApplication
public class OAuth2ResourceServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(OAuth2ResourceServerApplication.class, args);
    }

}
