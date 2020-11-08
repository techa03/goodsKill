package com.goodskill.mongo.listener;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.goodskill.mongo.entity.SuccessKilled;
import lombok.extern.slf4j.Slf4j;
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
    private final ReactiveMongoTemplate ops;

    public MongoMessageListener(ReactiveMongoTemplate ops) {
        this.ops = ops;
    }

    /**
     * 监听指定队列，并发数10-20
     *
     * @param message
     */
    @JmsListener(destination = "SUCCESS_KILLED_RESULT", concurrency = "10-20")
    @SentinelResource("SUCCESS_KILLED_RESULT")
    public void processMessage(Message message) {
        long seckillId = 0;
        String userPhone = null;
        try {
            seckillId = message.getLongProperty("seckillId");
            userPhone = message.getStringProperty("userPhone");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }
        ops.insert(SuccessKilled.builder().seckillId(BigInteger.valueOf(seckillId)).userPhone(userPhone).build())
                .doOnSuccess(n -> log.info("mongo秒杀记录插入成功:{}", n.toString())).subscribe();
        if (log.isDebugEnabled()) {
            log.debug("任务已结束！");
        }
    }

}
