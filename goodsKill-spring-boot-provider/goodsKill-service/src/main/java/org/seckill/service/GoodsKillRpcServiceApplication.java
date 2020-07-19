package org.seckill.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 * @author techa
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ImportResource(value = {
        "classpath*:META-INF/spring/spring-dao.xml"})
@EnableTransactionManagement
@MapperScan("org.seckill.mp.dao.mapper")
@RestController
public class GoodsKillRpcServiceApplication {
    @Autowired
    KafkaTemplate kafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(GoodsKillRpcServiceApplication.class, args);
    }

    @GetMapping("send")
    public void send() {
        kafkaTemplate.send("goodsKill-kafka", String.valueOf(1), String.valueOf("3434"));
    }

}