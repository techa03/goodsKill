package com.goodskill.mongoreactive.config;//package com.goodskill.mongoreactive.config;
//
//import com.goodskill.mongoreactive.listener.MongoMessageListener;
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.listener.DefaultMessageListenerContainer;
//
//import javax.jms.ConnectionFactory;
//import javax.jms.MessageListener;
//
///**
// * @author techa03
// * @date 2019/4/5
// */
////@Configuration
//public class JmsConfig {
//    @Autowired
//    MongoMessageListener mongoMessageListener;
//    @Bean
//    public ConnectionFactory activeMQConnectionFactory(){
//        return new ActiveMQConnectionFactory();
//    }
//
//    @Bean
//    public DefaultMessageListenerContainer defaultMessageListenerContainer() {
//        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
//        defaultMessageListenerContainer.setConnectionFactory(activeMQConnectionFactory());
//        defaultMessageListenerContainer.setDestinationName("GOODSKILL_MONGO_SENCE8");
////        defaultMessageListenerContainer.setConcurrency("100");
//        defaultMessageListenerContainer.setMessageListener(mongoMessageListener);
//        return defaultMessageListenerContainer;
//    }
//}
