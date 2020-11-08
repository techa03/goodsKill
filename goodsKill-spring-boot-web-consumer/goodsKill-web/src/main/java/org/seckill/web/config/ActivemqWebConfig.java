package org.seckill.web.config;

import lombok.Setter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.seckill.web.mqlistener.SeckillTopicListener;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * @author techa03
 * @date 2020/6/27
 */
@Configuration
@ConfigurationProperties(prefix = "spring.activemq")
public class ActivemqWebConfig {

    @Setter
    private String brokerUrl;

    private ActiveMQConnectionFactory targetConnectionFactory;

    @Bean
    public ActiveMQQueue queueDestination() {
        return new ActiveMQQueue("goodsKill");
    }

    @Bean
    public ActiveMQTopic topicDestination() {
        return new ActiveMQTopic("seckillTopic");
    }

    @Bean
    public JmsTemplate jmsTopicTemplate() {
        JmsTemplate jmsTopicTemplate = new JmsTemplate();
        jmsTopicTemplate.setConnectionFactory(targetConnectionFactory());
        jmsTopicTemplate.setDefaultDestination(topicDestination());
        return jmsTopicTemplate;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(targetConnectionFactory());
        jmsTemplate.setDefaultDestination(queueDestination());
        return jmsTemplate;
    }

    @Bean
    public DefaultMessageListenerContainer jmsContainer(SeckillTopicListener seckillTopicListener) {
        DefaultMessageListenerContainer jmsContainer = new DefaultMessageListenerContainer();
        jmsContainer.setConnectionFactory(targetConnectionFactory());
        jmsContainer.setDestination(topicDestination());
        jmsContainer.setMessageListener(seckillTopicListener);
        return jmsContainer;
    }

    public synchronized ActiveMQConnectionFactory targetConnectionFactory() {
        if (targetConnectionFactory != null ) {
            return targetConnectionFactory;
        }
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        this.targetConnectionFactory = connectionFactory;
        return connectionFactory;
    }

}
