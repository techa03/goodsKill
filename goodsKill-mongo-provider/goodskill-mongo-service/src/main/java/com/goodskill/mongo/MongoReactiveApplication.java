package com.goodskill.mongo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 * @author heng
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.goodskill.mongo.repository")
public class MongoReactiveApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MongoReactiveApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
