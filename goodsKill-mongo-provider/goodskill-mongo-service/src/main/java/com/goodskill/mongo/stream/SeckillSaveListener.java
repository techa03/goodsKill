package com.goodskill.mongo.stream;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.topic.SeckillMockSaveTopic;
import com.goodskill.mongo.vo.SeckillMockSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;

/**
 * 秒杀
 */
@Component
@Slf4j
public class SeckillSaveListener {
    @Autowired
    private SuccessKilledMongoService successKilledMongoService;

    @StreamListener(SeckillMockSaveTopic.INPUT)
    public void consume(SeckillMockSaveVo seckillMockSaveVo) {
        SuccessKilledDto successKilledDto = new SuccessKilledDto();
        successKilledDto.setSeckillId(BigInteger.valueOf(seckillMockSaveVo.getSeckillId()));
        successKilledDto.setUserPhone(seckillMockSaveVo.getUserPhone());
        successKilledDto.setCreateTime(new Date());
        successKilledMongoService.saveRecord(successKilledDto);
    }
}
