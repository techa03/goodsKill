package org.seckill.service;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类-简单依赖版，无需安装kafka也能启动
 * Created by techa03 on 2019/3/27.
 *
 * @author techa
 */
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class, ActiveMQAutoConfiguration.class, MongoAutoConfiguration.class})
@ImportResource(value = {"classpath*:META-INF/spring/spring-service.xml",
        "classpath*:META-INF/spring/spring-service-mq.xml",
        "classpath*:META-INF/spring/spring-dao.xml"})
@EnableTransactionManagement
public class GoodsKillRpcServiceSimpleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GoodsKillRpcServiceSimpleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}