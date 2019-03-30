package org.seckill.web.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author techa03
 * @date 2019/3/23
 */
@Configuration
@EnableRedisHttpSession
public class DataSourceConfig {
    @Value("${mq_address}")
    private String mq_address;

    @Bean
    public ActiveMQConnectionFactory targetConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(mq_address);
        return connectionFactory;
    }

}
