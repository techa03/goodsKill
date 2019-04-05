package com.goodskill.mongoreactive.listener;

import com.goodskill.mongoreactive.entity.SuccessKilled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.math.BigInteger;

/**
 * @author techa03
 * @date 2019/4/3
 */
@Component
@Slf4j
public class MessageListener {
    @Autowired
    ReactiveMongoTemplate ops;

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
        ops.insert(SuccessKilled.builder().seckillId(BigInteger.valueOf(seckillId)).userPhone(userPhone).build()).subscribe();
    }

}
