package com.goodskill.mongo.service;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilled;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.repository.SuceessKillRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author heng
 */
@DubboService
@Slf4j
@Primary
public class SuceessKillMongoServiceImpl implements SuccessKilledMongoService {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Autowired
    private SuceessKillRepository suceessKillRepository;

    @Override
    public void deleteRecord(long seckillId) {
        suceessKillRepository.deleteBySeckillId(BigInteger.valueOf(seckillId)).subscribe();
    }

    @Override
    public void saveRecord(SuccessKilledDto successKilledDto) {
        mongoTemplate.insert(SuccessKilled.builder().id(UUID.randomUUID().toString()).seckillId(successKilledDto.getSeckillId()).userPhone(successKilledDto.getUserPhone()).build())
                .doOnSuccess(n -> log.info("mongo秒杀记录插入成功:{}", n)).subscribe();
    }

    @Override
    public long count(long seckillId) {
        return suceessKillRepository.findBySeckillId(BigInteger.valueOf(seckillId)).count().block();
    }

}