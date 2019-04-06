package com.goodskill.mongo.service.listener;

import com.goodskill.mongo.entity.SuccessKilledDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.math.BigInteger;

/**
 * mq监听器，监听秒杀成功消息，收到消息插入mongo，使用reactive nio模型
 *
 * @author techa03
 * @date 2019/4/3
 */
@Component
@Slf4j
public class MongoMessageListener {
    @Autowired
    ReactiveMongoTemplate ops;

    /**
     * 监听指定队列，并发数10-20
     *
     * @param message
     */
    @JmsListener(destination = "GOODSKILL_MONGO_SENCE8", concurrency = "10-20")
    public void processMessage(Message message) {
        long seckillId = 0;
        String userPhone = null;
        String note = null;
        try {
            seckillId = message.getLongProperty("seckillId");
            userPhone = message.getStringProperty("userPhone");
            note = message.getStringProperty("note");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }
        ops.insert(SuccessKilledDto.builder().seckillId(BigInteger.valueOf(seckillId)).userPhone(userPhone).build())
                .doOnSuccess(n -> log.info("mongo秒杀记录插入成功:{}", n.toString())).subscribe();
        if (log.isDebugEnabled()) {
            log.debug("任务已结束！");
        }
    }

}
