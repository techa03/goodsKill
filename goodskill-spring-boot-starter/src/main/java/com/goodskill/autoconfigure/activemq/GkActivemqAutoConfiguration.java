package com.goodskill.autoconfigure.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;

@Configuration
@EnableConfigurationProperties({ActiveMQProperties.class})
@AutoConfigureAfter(ActiveMQAutoConfiguration.class)
public class GkActivemqAutoConfiguration {

//    @Bean
//    @Primary
//    public ActiveMQConnectionFactory targetConnectionFactory(ActiveMQProperties activeMQProperties) {
//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
//        String brokerUrl = activeMQProperties.getBrokerUrl();
//        if (StringUtils.isEmpty(brokerUrl)) {
//            brokerUrl = "tcp://localhost:61616";
//        }
//        connectionFactory.setBrokerURL(brokerUrl);
//        return connectionFactory;
//    }

    @Bean
    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    public JmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                                   @Autowired ActiveMQConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, activeMQConnectionFactory);
        return factory;
    }

    @Bean
    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    public CachingConnectionFactory cachingConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(200);
        return cachingConnectionFactory;
    }


    @Bean
    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    public SingleConnectionFactory connectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);
        return singleConnectionFactory;
    }

}
