package org.seckill.service;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 * @author techa
 */
@SpringBootApplication
@ImportResource(value = {"classpath*:META-INF/spring/spring-service.xml",
        "classpath*:META-INF/spring/spring-service-mq.xml",
        "classpath*:META-INF/spring/spring-dao.xml"})
@EnableTransactionManagement
public class GoodsKillRpcServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GoodsKillRpcServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}