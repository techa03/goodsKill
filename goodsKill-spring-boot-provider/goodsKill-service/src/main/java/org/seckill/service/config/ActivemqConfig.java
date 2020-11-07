package org.seckill.service.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.seckill.service.mq.SeckillActiveConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public ActiveMQQueue queueDestination() {
        return new ActiveMQQueue("goodsKill");
    }

    @Bean
    public ActiveMQTopic topicDestination() {
        return new ActiveMQTopic("seckillTopic");
    }

    @Bean
    public DefaultMessageListenerContainer jmsContainer(SeckillActiveConsumer seckillActiveConsumer,
                                                        SingleConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setConnectionFactory(connectionFactory);
        jmsContainer.setDestination(queueDestination());
        jmsContainer.setMessageListener(seckillActiveConsumer);
        return jmsContainer;
    }


    @Bean
    public JmsTemplate jmsTopicTemplate(SingleConnectionFactory connectionFactory) {
        JmsTemplate jmsTopicTemplate = new JmsTemplate();
        jmsTopicTemplate.setConnectionFactory(connectionFactory);
        jmsTopicTemplate.setDefaultDestination(topicDestination());
        return jmsTopicTemplate;
    }

    @Bean
    public JmsTemplate jmsTemplate(SingleConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestination(queueDestination());
        return jmsTemplate;
    }

    @Bean
    public ThreadPoolExecutor taskExecutor() {
        return new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
