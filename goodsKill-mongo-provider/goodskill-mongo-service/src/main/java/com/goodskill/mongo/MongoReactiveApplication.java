package com.goodskill.mongo;

import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.service.impl.MongoReactiveServiceImpl;
import com.goodskill.mongo.vo.SeckillMockSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author heng
 */
@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = "com.goodskill.mongo.repository")
public class MongoReactiveApplication {

    @Autowired
    private MongoReactiveServiceImpl mongoReactiveService;

    public static void main(String[] args) {
        SpringApplication.run(MongoReactiveApplication.class, args);
    }

    /**
     * 监听seckillMongoSave队列消息
     *
     * @return
     */
    @Bean
    public Consumer<Flux<SeckillMockSaveVo>> seckillMongoSave() {
        return saveVoFlux -> saveVoFlux.flatMap(it -> {
            SuccessKilledDto successKilledDto = new SuccessKilledDto();
            successKilledDto.setSeckillId(BigInteger.valueOf(it.getSeckillId()));
            successKilledDto.setUserPhone(it.getUserPhone());
            successKilledDto.setCreateTime(new Date());
            return mongoReactiveService.saveRecord(successKilledDto);
        }).subscribe();
    }

}
