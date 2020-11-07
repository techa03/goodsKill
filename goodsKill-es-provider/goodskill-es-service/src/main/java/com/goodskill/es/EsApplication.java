package com.goodskill.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * elasticsearch服务启动类
 * @author heng
 * @date 2019/06/15
 */
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.goodskill.es.repository")
@EnableFeignClients(basePackages = "com.goodskill.*.api")
@EnableDiscoveryClient
public class EsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }

}
