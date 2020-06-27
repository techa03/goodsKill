package org.seckill.service.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.seckill.service.mq.SeckillActiveConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author techa03
 * @date 2020/6/27
 */
@Configuration
public class ActivemqConfig {
    @Value("${mq_address}")
    private String mqAddress;

    @Bean
    public ActiveMQConnectionFactory targetConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(mqAddress);
        return connectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, targetConnectionFactory());
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(targetConnectionFactory());
        cachingConnectionFactory.setSessionCacheSize(200);
        return cachingConnectionFactory;
    }

    @Bean
    public ActiveMQQueue queueDestination() {
        return new ActiveMQQueue("goodsKill");
    }

    @Bean
    public ActiveMQTopic topicDestination() {
        return new ActiveMQTopic("seckillTopic");
    }

    @Bean
    public DefaultMessageListenerContainer jmsContainer(SeckillActiveConsumer seckillActiveConsumer) {
        DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setConnectionFactory(connectionFactory());
        jmsContainer.setDestination(queueDestination());
        jmsContainer.setMessageListener(seckillActiveConsumer);
        return jmsContainer;
    }

    @Bean
    public SingleConnectionFactory connectionFactory() {
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(targetConnectionFactory());
        return singleConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTopicTemplate() {
        JmsTemplate jmsTopicTemplate = new JmsTemplate();
        jmsTopicTemplate.setConnectionFactory(connectionFactory());
        jmsTopicTemplate.setDefaultDestination(topicDestination());
        return jmsTopicTemplate;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setDefaultDestination(queueDestination());
        return jmsTemplate;
    }

    @Bean
    public ThreadPoolExecutor taskExecutor() {
        return new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
