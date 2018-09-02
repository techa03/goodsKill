package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Date;

@Slf4j
public class SeckillActiveConsumer implements MessageListener {
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Autowired
    private MqTask mqTask;

    @Override
    public void onMessage(Message message) {
        long seckillId = 0;
        String userPhone = null;
        try {
            seckillId = message.getLongProperty("seckillId");
            userPhone = message.getStringProperty("userPhone");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }
        Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
        if (seckill.getNumber() > 0) {
            extSeckillMapper.reduceNumber(seckillId, new Date());
            SuccessKilled record = new SuccessKilled();
            record.setSeckillId(seckillId);
            record.setUserPhone(userPhone);
            record.setStatus((byte) 1);
            record.setCreateTime(new Date());
            successKilledMapper.insert(record);
        } else {
            mqTask.sendSeckillSuccessTopic(seckillId,"秒杀场景三(activemq消息队列实现)");
            if (log.isDebugEnabled()) {
                log.debug("库存不足，无法继续秒杀！");
            }
        }
    }


}
