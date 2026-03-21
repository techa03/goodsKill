package com.goodskill.web.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    /**
     * 声明一个名为goodskill-kafka的Topic，12个分区，1个副本
     * @return
     */
    @Bean
    public NewTopic myTopic() {
        return TopicBuilder.name("goodskill-kafka")
                .partitions(12)
                .replicas(1)
                .build();
    }

}
