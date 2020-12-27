package org.seckill.service;

import com.goodskill.mongo.topic.SeckillMockSaveTopic;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 *
 * @author techa
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ImportResource(value = {
        "classpath*:META-INF/spring/spring-dao.xml"})
@EnableTransactionManagement
@MapperScan("org.seckill.mp.dao.mapper")
@EnableDiscoveryClient
@EnableBinding(value = {Sink.class, Source.class, SeckillMockSaveTopic.class})
@EnableFeignClients("com.goodskill.*.api")
public class GoodsKillRpcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsKillRpcServiceApplication.class, args);
    }


}