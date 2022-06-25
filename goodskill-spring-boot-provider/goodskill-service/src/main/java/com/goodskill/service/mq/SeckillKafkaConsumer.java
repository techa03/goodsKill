package com.goodskill.service.mq;

import com.alibaba.fastjson2.JSON;
import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.service.inner.SeckillExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * kafka秒杀请求监听器
 *
 * @author heng
 * @date 18/09/02
 */
@Slf4j
@Component
public class SeckillKafkaConsumer {
    @Autowired
    private SeckillExecutor seckillExecutor;

    @KafkaListener(topics = "goodskill-kafka")
    public void onMessage(Object data) {
        if (data instanceof ConsumerRecord) {
            ConsumerRecord record = (ConsumerRecord) data;
            String userPhone = record.key().toString();
            String value = (String) record.value();
            SeckillMockRequestDTO seckillMockRequestDTO;
            if (StringUtils.hasText(value)) {
                seckillMockRequestDTO = JSON.parseObject(value, SeckillMockRequestDTO.class);
                seckillExecutor.dealSeckill(seckillMockRequestDTO.getSeckillId(), userPhone, "秒杀场景四(kafka消息队列实现)", seckillMockRequestDTO.getTaskId());
            }
        } else {
            log.warn("未处理数据：{}", data.toString());
        }
    }


}
