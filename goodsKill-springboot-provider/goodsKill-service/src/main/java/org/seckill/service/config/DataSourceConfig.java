package org.seckill.service.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
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

import java.sql.SQLException;

/**
 * @author techa03
 * @date 2019/3/23
 */
@Configuration
public class DataSourceConfig {
    @Value("${mq_address}")
    private String mq_address;

    @Bean
    public DruidDataSource masterDataSource(@Value("${master.driver}") String driverClassName,
                                            @Value("${master.url}") String url,
                                            @Value("${master.username}") String username,
                                            @Value("${master.password}") String password) throws SQLException {
        DruidDataSource master = new DruidDataSource();
        master.setDriverClassName(driverClassName);
        master.setUrl(url);
        master.setUsername(username);
        master.setPassword(AESUtil.aesDecode(password));
        master.setFilters("stat,slf4j");
        return master;
    }

    @Bean
    public DruidDataSource slaveDataSource(@Value("${slave.driver}") String driverClassName,
                                           @Value("${slave.url}") String url,
                                           @Value("${slave.username}") String username,
                                           @Value("${slave.password}") String password) throws SQLException {
        DruidDataSource master = new DruidDataSource();
        master.setDriverClassName(driverClassName);
        master.setUrl(url);
        master.setUsername(username);
        master.setPassword(AESUtil.aesDecode(password));
        master.setFilters("stat,slf4j");
        return master;
    }

    @Bean
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(10000);
        statFilter.setLogSlowSql(true);
        return statFilter;
    }

    @Bean
    public ActiveMQConnectionFactory targetConnectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(mq_address);
        return connectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory(
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
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
