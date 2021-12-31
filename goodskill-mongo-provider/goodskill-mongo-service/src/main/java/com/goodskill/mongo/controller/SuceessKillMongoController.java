package com.goodskill.mongo.controller;

import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.service.impl.MongoReactiveServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author heng
 */
@Slf4j
@RestController
public class SuceessKillMongoController {
    @Autowired
    private MongoReactiveServiceImpl mongoReactiveService;

    @DeleteMapping("/deleteRecord")
    public Mono<Boolean> deleteRecord(long seckillId) {
        return mongoReactiveService.deleteRecord(seckillId);
    }

    @PostMapping("/saveRecord")
    public Mono<Boolean> saveRecord(SuccessKilledDto successKilledDto) {
        return mongoReactiveService.saveRecord(successKilledDto);
    }

    @GetMapping("/count")
    public Mono<Long> count(long seckillId) {
        return mongoReactiveService.count(seckillId);
    }

}