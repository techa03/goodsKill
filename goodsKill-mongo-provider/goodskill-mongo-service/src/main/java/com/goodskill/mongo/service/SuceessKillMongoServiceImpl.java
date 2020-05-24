package com.goodskill.mongo.service;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilled;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author heng
 */
@Service(version = "1.0.0")
@Slf4j
public class SuceessKillMongoServiceImpl implements SuccessKilledMongoService {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public long deleteRecord(long sekcillId) {
        AtomicLong deleteCount = new AtomicLong();
        mongoTemplate
                .remove(query(where("seckillId").is(String.valueOf(sekcillId))), SuccessKilledDto.class)
                .map(DeleteResult::getDeletedCount).doOnSuccess(deleteCount::set).defaultIfEmpty(0L)
                .subscribe();
        return deleteCount.get();
    }

    @Override
    public void saveRecord(SuccessKilledDto successKilledDto) {
        mongoTemplate.insert(SuccessKilled.builder().seckillId(successKilledDto.getSeckillId()).userPhone(successKilledDto.getUserPhone()).build())
                .doOnSuccess(n -> log.info("mongo秒杀记录插入成功:{}", n)).subscribe();
    }

    @Override
    public long count(SuccessKilledDto successKilledDto) {
        AtomicLong count = new AtomicLong();
        mongoTemplate
                .count(query(where("seckillId").is(successKilledDto.getSeckillId())), SuccessKilledDto.class)
                .doOnSuccess(count::set)
                .defaultIfEmpty(0L)
                .subscribe();
        return count.get();
    }
}