package com.goodskill.autoconfigure.mysql;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * @author techa03
 * @date 2019/4/7
 */
@Configuration
@EnableConfigurationProperties(MysqlDruidProperties.class)
public class GoodskillMysqlAutoConfiguration {
    @Bean
    public DruidDataSource masterDataSource(@Value("${master.driver}") String driverClassName,
                                            @Value("${master.url}") String url,
                                            @Value("${master.username}") String username,
                                            @Value("${master.password}") String password) throws SQLException {
        DruidDataSource master = new DruidDataSource();
        master.setDriverClassName(driverClassName);
        master.setUrl(url);
        master.setUsername(username);
        master.setPassword(password);
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
        master.setPassword(password);
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
}
