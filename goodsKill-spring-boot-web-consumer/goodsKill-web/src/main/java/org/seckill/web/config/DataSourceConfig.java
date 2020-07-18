package org.seckill.web.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import javax.validation.ValidatorFactory;

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

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(targetConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    @Bean
    @Primary
    public ValidatorFactory getValidatorFactory() {
        return new OptionalValidatorFactoryBean();
    }
}
