package org.seckill.service.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * mongo开启事务支持配置类
 *
 * @author heng
 * @data 2019/03/09
 * <strong>Note:</strong> mongo4.0开始支持事务管理，是基于副本集的（mongodb副本集群搭建教程自行查询）,mongo4.2支持分片集群事务
 *
 */
@Configuration
public class MongoTransactionConfig extends AbstractMongoConfiguration {

    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory mongoDbFactory) {
        return new MongoTransactionManager(mongoDbFactory);
    }

    @Override
    public MongoClient mongoClient() {
        MongoClient mongoClient = new MongoClient();
        return mongoClient;
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }
}
