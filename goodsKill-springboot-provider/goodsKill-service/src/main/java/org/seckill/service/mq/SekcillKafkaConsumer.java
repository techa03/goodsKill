package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀请求监听器
 * Created by heng on 18/09/02.
 */
@Slf4j
@Component
public class SekcillKafkaConsumer extends AbstractMqConsumer {
    @KafkaListener(topics = "goodsKill-kafka")
    public void onMessage(Object data) {
        if (data instanceof ConsumerRecord) {
            ConsumerRecord record = (ConsumerRecord) data;
            String userPhone = record.key().toString();
            long seckillId = Long.parseLong((String) record.value());
            dealSeckill(seckillId,userPhone,"秒杀场景四(kafka消息队列实现)");
        } else {
            log.info("未处理数据：{}", data.toString());
        }
    }
}
