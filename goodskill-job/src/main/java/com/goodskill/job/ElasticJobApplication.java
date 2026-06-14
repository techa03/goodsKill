package com.goodskill.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author techa03
 */
@SpringBootApplication
public class ElasticJobApplication {

    public static void main(String[] args) {
        new SpringApplication(ElasticJobApplication.class).run(args);
    }
}
