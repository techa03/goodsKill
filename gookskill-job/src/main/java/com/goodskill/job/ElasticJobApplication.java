package com.goodskill.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author techa03
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients("com.goodskill.*.api")
public class ElasticJobApplication {

    public static void main(String[] args) {
        new SpringApplication(ElasticJobApplication.class).run(args);
    }
}
