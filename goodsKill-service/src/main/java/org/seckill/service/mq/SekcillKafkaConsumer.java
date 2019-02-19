package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

/**
 * 秒杀请求监听器
 * Created by heng on 18/09/02.
 */
@Slf4j
public class SekcillKafkaConsumer implements MessageListener {
    @Autowired
    private SeckillExecutor seckillExecutor;

    @Override
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
}
