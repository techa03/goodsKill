package com.goodskill.mongo.controller;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author heng
 */
@Slf4j
@RestController
public class SuceessKillMongoController implements SuccessKilledMongoService {
    @Autowired
    private SuccessKilledMongoService successKilledMongoService;

    @Override
    public void deleteRecord(long seckillId) {
        successKilledMongoService.deleteRecord(seckillId);
    }

    @Override
    public void saveRecord(SuccessKilledDto successKilledDto) {
        successKilledMongoService.saveRecord(successKilledDto);
    }

    @Override
    public long count(long seckillId) {
        return successKilledMongoService.count(seckillId);
    }

}