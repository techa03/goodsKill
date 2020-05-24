package com.goodskill.mongo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 * @author heng
 */
@SpringBootApplication
public class MongoReactiveApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MongoReactiveApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
