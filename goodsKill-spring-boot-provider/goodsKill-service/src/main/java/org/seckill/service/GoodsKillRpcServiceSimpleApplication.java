package org.seckill.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class, MongoAutoConfiguration.class, DataSourceAutoConfiguration.class})
@ImportResource(value = {
        "classpath*:META-INF/spring/spring-dao.xml"})
@EnableTransactionManagement
@MapperScan("org.seckill.mp.dao.mapper")
public class GoodsKillRpcServiceSimpleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GoodsKillRpcServiceSimpleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}