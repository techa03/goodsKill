package com.goodskill.service;

import com.alibaba.cloud.seata.feign.SeataFeignClientAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 *
 * @author techa
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SeataFeignClientAutoConfiguration.class})
@EnableTransactionManagement
@MapperScan("com.goodskill.mp.dao.mapper")
@EnableDiscoveryClient
@EnableFeignClients({"com.goodskill.mongo.api", "com.goodskill.es.api"})
@RestController
public class GoodsKillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsKillServiceApplication.class, args);
    }


}