package org.seckill.service.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * mongo配置类，支持repository方式访问mongodb
 * @author heng
 * @date 2019/03/09
 */
@Configuration
@EnableMongoRepositories("org.seckill.dao.mongo")
public class ApplicationConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "test";
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