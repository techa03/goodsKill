package com.goodskill.mongo.controller;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilled;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author heng
 */
@Slf4j
@RestController
public class SuceessKillMongoController implements SuccessKilledMongoService {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public long deleteRecord(long sekcillId) {
        AtomicLong deleteCount = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mongoTemplate
                .remove(query(where("seckillId").is(String.valueOf(sekcillId))), SuccessKilled.class)
                .map(DeleteResult::getDeletedCount)
                .doOnSuccess(it -> {
                    log.info("删除秒杀成功记录数:{}", it);
                    deleteCount.set(it);
                    countDownLatch.countDown();
                })
                .defaultIfEmpty(0L)
                .subscribe();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mongoTemplate.count(query(where("seckillId").is(successKilledDto.getSeckillId())), SuccessKilled.class)
                .doOnSuccess(it -> {
                    log.info("秒杀成功数:{}", it);
                    count.set(it);
                    countDownLatch.countDown();
                }).subscribe();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return count.get();
    }

    @GetMapping("/counttest")
    public long count(@RequestParam(value = "red2") String red) {
        AtomicLong count = new AtomicLong();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mongoTemplate.count(query(where("seckillId").is(red)), SuccessKilled.class)
                .doOnSuccess(it -> {
                    log.info("秒杀成功数:{}", it);
                    count.set(it);
                    countDownLatch.countDown();
                }).subscribe();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return count.get();
    }

}