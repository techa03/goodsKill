package org.seckill.service.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

/**
 * @author techa03
 * @date 2019/3/23
 */
//@Configuration
@Slf4j
public class DataSourceTestConfig {
    @Value("${spring.shardingsphere.datasource.ds0.driver-class-name}")
    private String driverClassName0;
    @Value("${spring.shardingsphere.datasource.ds0.jdbcUrl}")
    private String jdbcUrl0;
    @Value("${spring.shardingsphere.datasource.ds0.username}")
    private String username0;
    @Value("${spring.shardingsphere.datasource.ds0.password}")
    private String password0;

    @Value("${spring.shardingsphere.datasource.ds1.driver-class-name}")
    private String driverClassName1;
    @Value("${spring.shardingsphere.datasource.ds1.jdbcUrl}")
    private String jdbcUrl1;
    @Value("${spring.shardingsphere.datasource.ds1.username}")
    private String username1;
    @Value("${spring.shardingsphere.datasource.ds1.password}")
    private String password1;

    @Bean
    @Profile({"test"})
    public HikariDataSource ds0() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName0);
        dataSource.setJdbcUrl(jdbcUrl0);
        dataSource.setPassword(password0);
        dataSource.setUsername(username0);
        Properties dsProperties = new Properties();
        dsProperties.setProperty("schema", "classpath*:/schema-h2.sql");
        dsProperties.setProperty("data", "classpath*:/data-h2.sql");
        dataSource.setDataSourceProperties(dsProperties);
        return dataSource;
    }

    @Bean
    @Profile({"test"})
    public HikariDataSource ds1() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName1);
        dataSource.setJdbcUrl(jdbcUrl1);
        dataSource.setPassword(password1);
        dataSource.setUsername(username1);
        Properties dsProperties = new Properties();
        dsProperties.setProperty("schema", "classpath*:/schema-h2.sql");
        dsProperties.setProperty("data", "classpath*:/data-h2.sql");
        dataSource.setDataSourceProperties(dsProperties);
        return dataSource;
    }

}
