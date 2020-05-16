package org.seckill.service.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.seckill.util.common.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author techa03
 * @date 2019/3/23
 */
@Configuration
@Slf4j
public class DataSourceConfig {
    @Value("${mq_address}")
    private String mqAddress;

    @Bean
    public HikariDataSource masterDataSource(@Value("${master.driver}") String driverClassName,
                                             @Value("${master.url}") String url,
                                             @Value("${master.username}") String username,
                                             @Value("${master.password}") String password) {
        HikariDataSource master = getDataSource(driverClassName, url, username, password);
        return master;
    }

    @Bean
    public HikariDataSource slaveDataSource(@Value("${slave.driver}") String driverClassName,
                                            @Value("${slave.url}") String url,
                                            @Value("${slave.username}") String username,
                                            @Value("${slave.password}") String password) {
        HikariDataSource slave = getDataSource(driverClassName, url, username, password);
        return slave;
    }

    /**
     * 获取datasource
     *
     * @param driverClassName 驱动类
     * @param url             数据库url
     * @param username        用户名
     * @param password        密码
     * @return druid数据源
     */
    private HikariDataSource getDataSource(@Value("${slave.driver}") String driverClassName, @Value("${slave.url}") String url, @Value("${slave.username}") String username, @Value("${slave.password}") String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(AESUtil.aesDecode(password));
        return dataSource;
    }

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
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

}
