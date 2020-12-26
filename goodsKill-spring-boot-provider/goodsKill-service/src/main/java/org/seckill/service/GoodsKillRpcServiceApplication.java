package org.seckill.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableDiscoveryClient
@EnableFeignClients("com.goodskill.*.api")
public class GoodsKillRpcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsKillRpcServiceApplication.class, args);
    }


}