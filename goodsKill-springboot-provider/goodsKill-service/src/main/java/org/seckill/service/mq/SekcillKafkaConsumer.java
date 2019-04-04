package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.service.common.RedisService;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * 秒杀请求监听器
 * Created by heng on 18/09/02.
 */
@Slf4j
@Component
public class SekcillKafkaConsumer {
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    SeckillExecutor seckillExecutor;
    @Autowired
    MqTask mqTask;

    @KafkaListener(topics = "goodsKill-kafka")
    public void onMessage(Object data) {
        if (data instanceof ConsumerRecord) {
            ConsumerRecord record = (ConsumerRecord) data;
            String userPhone = record.key().toString();
            long seckillId = Long.parseLong((String) record.value());
            seckillExecutor.dealSeckill(seckillId, userPhone, "秒杀场景四(kafka消息队列实现)");
        } else {
            log.info("未处理数据：{}", data.toString());
        }
    }

    @JmsListener(destination = "GOODSKILL_SENCE8")
    public void processMessage(Message message) {
        log.debug("收到消息{}", message.toString());
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
        dealSeckill(seckillId, userPhone, note);

    }

    public void dealSeckill(long seckillId, String userPhone, String note) {
        Seckill seckill = redisService.getSeckill(seckillId);
        long number = redisTemplate.opsForValue().increment(seckillId);
        if (number < seckill.getNumber()) {
            taskExecutor.execute(() ->
                    jmsTemplate.send("GOODSKILL_MONGO_SENCE8", session -> {
                        Message message = session.createMessage();
                        message.setLongProperty("seckillId", seckillId);
                        message.setStringProperty("userPhone", String.valueOf(number));
                        message.setStringProperty("note", "秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)");
                        return message;
                    })
            );
        } else {
            if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                mqTask.sendSeckillSuccessTopic(seckillId, note);
                Seckill sendTopicResult = new Seckill();
                sendTopicResult.setSeckillId(seckillId);
                sendTopicResult.setStatus(SeckillStatusConstant.END);
                extSeckillMapper.updateByPrimaryKeySelective(sendTopicResult);
                seckill.setStatus(SeckillStatusConstant.END);
                redisService.putSeckill(seckill);
            }
        }
    }
}
