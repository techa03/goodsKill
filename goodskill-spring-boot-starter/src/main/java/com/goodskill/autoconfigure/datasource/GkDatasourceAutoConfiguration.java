package com.goodskill.autoconfigure.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sharding-jdbc自动多数据源配置，目前暂不支持主从
 *
 * @author techa03
 * @date 2020/9/20
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GkDatasourceProperties.class)
public class GkDatasourceAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.shardingsphere.datasource.ds0", name = "type",
            havingValue = "com.zaxxer.hikari.HikariDataSource")
    public HikariDataSource ds0(GkDatasourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(properties.getDs0().getUsername());
        dataSource.setDriverClassName(properties.getDs0().getDriverClassName());
        dataSource.setJdbcUrl(properties.getDs0().getJdbcUrl());
        dataSource.setPassword(properties.getDs0().getPassword());
        return dataSource;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.shardingsphere.datasource.ds1", name = "type",
            havingValue = "com.zaxxer.hikari.HikariDataSource")
    public HikariDataSource ds1(GkDatasourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(properties.getDs1().getUsername());
        dataSource.setDriverClassName(properties.getDs1().getDriverClassName());
        dataSource.setJdbcUrl(properties.getDs1().getJdbcUrl());
        dataSource.setPassword(properties.getDs1().getPassword());
        return dataSource;
    }
}
