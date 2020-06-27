package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * kafka秒杀请求监听器
 *
 * @author heng
 * @date 18/09/02
 */
@Slf4j
@Component
public class SekcillKafkaConsumer {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private ThreadPoolExecutor taskExecutor;
    @Autowired
    private SeckillExecutor seckillExecutor;
    @Autowired
    private ActiveMqMessageSender activeMqMessageSender;

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

    /**
     * 此为ActiveMq消息监听器（监听模拟秒杀场景八消息队列）
     *
     * @param message 消息
     */
    @JmsListener(destination = "GOODSKILL_SENCE8")
    public void processMessage(Message message) {
        log.debug("收到消息{}", message);
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

    /**
     * 内部处理秒杀请求
     *
     * @param seckillId 秒杀活动id
     * @param userPhone 用户手机号
     * @param note      备注
     */
    private void dealSeckill(long seckillId, String userPhone, String note) {
        log.info("seckillId:{},userphone:{}", seckillId, userPhone);
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
                log.info("秒杀商品暂无库存，发送活动结束消息！");
                activeMqMessageSender.sendSeckillSuccessTopic(seckillId, note);
                Seckill sendTopicResult = new Seckill();
                sendTopicResult.setSeckillId(seckillId);
                sendTopicResult.setStatus(SeckillStatusConstant.END);
                seckillMapper.updateById(sendTopicResult);
                seckill.setStatus(SeckillStatusConstant.END);
                redisService.putSeckill(seckill);
            }
        }
    }
}
