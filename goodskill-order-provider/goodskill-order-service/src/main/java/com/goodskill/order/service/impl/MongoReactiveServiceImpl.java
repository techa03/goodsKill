package com.goodskill.order.service.impl;

import com.goodskill.order.entity.SuccessKilled;
import com.goodskill.order.entity.SuccessKilledDto;
import com.goodskill.order.repository.SuceessKillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author heng
 */
@Slf4j
@Service
public class MongoReactiveServiceImpl {
    @Autowired
    private SuceessKillRepository suceessKillRepository;

    public Mono<Boolean> deleteRecord(long seckillId) {
        return suceessKillRepository.deleteBySeckillId(BigInteger.valueOf(seckillId)).thenReturn(true);
    }

    public Mono<Boolean> saveRecord(SuccessKilledDto successKilledDto) {
        return suceessKillRepository.insert(SuccessKilled.builder()
                        .id(UUID.randomUUID().toString())
                        .seckillId(successKilledDto.getSeckillId())
                        .userPhone(successKilledDto.getUserPhone())
                        .build())
                .doOnSuccess(n -> log.info("mongo秒杀记录插入成功:{}", n)).thenReturn(true);
    }

    public Mono<Long> count(long seckillId) {
        return suceessKillRepository.findBySeckillId(BigInteger.valueOf(seckillId)).count();
    }

}
