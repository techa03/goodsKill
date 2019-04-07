package org.seckill.service.config;

import com.mongodb.MongoClient;
import lombok.Data;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * mongo配置类，支持repository方式访问mongodb
 * <strong>Note:</strong> mongo4.0开始支持事务管理，是基于副本集的（mongodb副本集群搭建教程自行查询）,mongo4.2支持分片集群事务
 * @author techa
 * @date 2019/03/09
 */
//@Configuration
//@EnableMongoRepositories("org.seckill.dao.mongo")
//@ConfigurationProperties(prefix = "spring.data.mongodb")
@Data
public class ApplicationConfig extends AbstractMongoConfiguration {
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient();
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Stream.of("org.seckill.entity").collect(Collectors.toList());
    }
}