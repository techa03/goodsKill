package com.goodskill.mongo;

import com.goodskill.mongo.topic.SeckillMockSaveTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;

/**
 *
 * @author heng
 */
@SpringBootApplication
@EnableBinding(value = {SeckillMockSaveTopic.class, Sink.class, Source.class})
public class MongoReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoReactiveApplication.class, args);
    }

}
