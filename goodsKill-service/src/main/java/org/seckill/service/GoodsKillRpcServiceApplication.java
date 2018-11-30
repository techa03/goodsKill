package org.seckill.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 */
@SpringBootApplication
@ImportResource(value = {"classpath*:META-INF/spring/spring-*.xml"})
public class GoodsKillRpcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsKillRpcServiceApplication.class);
    }

}