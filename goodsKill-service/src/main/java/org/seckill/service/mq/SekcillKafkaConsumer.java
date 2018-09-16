package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import java.util.Date;

/**
 * 秒杀请求监听器
 * Created by heng on 18/09/02.
 */
@Slf4j
public class SekcillKafkaConsumer implements MessageListener {
    @Autowired
    private ExtSeckillMapper extSeckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private MqTask mqTask;

    @Override
    public void onMessage(Object data) {
        if (data instanceof ConsumerRecord) {
            ConsumerRecord record = (ConsumerRecord) data;
            String userPhone = record.key().toString();
            long seckillId = Long.parseLong((String) record.value());
            Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
            if (seckill.getNumber() > 0) {
                extSeckillMapper.reduceNumber(seckillId, new Date());
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserPhone(userPhone);
                successKilled.setStatus((byte) 1);
                successKilled.setCreateTime(new Date());
                successKilledMapper.insert(successKilled);
            } else {
                mqTask.sendSeckillSuccessTopic(seckillId, "秒杀场景四(kafka消息队列实现)");
                if (log.isDebugEnabled()) {
                    log.debug("库存不足，无法继续秒杀！");
                }
            }
        } else {
            log.info("未处理数据：{}", data.toString());
        }
    }
}
