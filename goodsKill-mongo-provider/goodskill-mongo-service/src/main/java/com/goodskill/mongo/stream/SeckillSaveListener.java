package com.goodskill.mongo.stream;

import com.goodskill.mongo.api.SuccessKilledMongoService;
import com.goodskill.mongo.entity.SuccessKilledDto;
import com.goodskill.mongo.topic.SeckillMockSaveTopic;
import com.goodskill.mongo.vo.SeckillMockSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

/**
 * 秒杀
 */
@Component
@Slf4j
public class SeckillSaveListener {
    @Autowired
    private SuccessKilledMongoService successKilledMongoService;

    @StreamListener(SeckillMockSaveTopic.INPUT)
    public void consume(SeckillMockSaveVo seckillMockSaveVo) throws InterruptedException {
        if (true) {
            Thread.sleep(3000L);
            throw new RuntimeException();
        }
        SuccessKilledDto successKilledDto = new SuccessKilledDto();
        successKilledDto.setSeckillId(BigInteger.valueOf(seckillMockSaveVo.getSeckillId()));
        successKilledDto.setUserPhone(seckillMockSaveVo.getUserPhone());
        successKilledDto.setCreateTime(new Date());
        successKilledMongoService.saveRecord(successKilledDto);
    }

    @StreamListener(Sink.INPUT)
    public void listen(String in, @Header(name = "x-death", required = false) Map<?,?> death) {
        if (death != null && death.get("count").equals(3L)) {
            // giving up - don't send to DLX
            throw new ImmediateAcknowledgeAmqpException("Failed after 4 attempts");
        }
        throw new AmqpRejectAndDontRequeueException("failed");
    }


}
