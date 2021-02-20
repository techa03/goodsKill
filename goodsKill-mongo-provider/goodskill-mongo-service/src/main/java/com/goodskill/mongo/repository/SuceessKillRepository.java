package com.goodskill.mongo.repository;

import com.goodskill.mongo.entity.SuccessKilled;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface SuceessKillRepository extends ReactiveMongoRepository<SuccessKilled, String> {

    Mono<Void> deleteBySeckillId(BigInteger seckillId);

    Flux<SuccessKilled> findBySeckillId(BigInteger seckillId);
}
