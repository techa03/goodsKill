package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    private SeckillExecutor seckillExecutor;

    @KafkaListener(topics = "goodsKill-kafka")
    public void onMessage(Object data) {
        if (data instanceof ConsumerRecord) {
            ConsumerRecord record = (ConsumerRecord) data;
            String userPhone = record.key().toString();
            long seckillId = Long.parseLong((String) record.value());
            seckillExecutor.dealSeckill(seckillId, userPhone, "秒杀场景四(kafka消息队列实现)");
        } else {
            log.warn("未处理数据：{}", data.toString());
        }
    }


}
