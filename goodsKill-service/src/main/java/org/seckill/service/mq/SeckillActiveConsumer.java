package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.SeckillService;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.Date;

import static org.seckill.service.mq.MqTask.taskCompleteFlag;

@Slf4j
public class SeckillActiveConsumer implements MessageListener {
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTopicTemplate;
    @Autowired
    private SeckillService seckillService;

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
            if (MqTask.count == 0) {
                seckillService.getSuccessKillCount(seckillId);
                long finalSeckillId = seckillId;
                jmsTopicTemplate.send(new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        taskCompleteFlag = true;
                        Message message = session.createMessage();
                        message.setLongProperty("seckillId", finalSeckillId);
                        message.setBooleanProperty("status", taskCompleteFlag);
                        return message;
                    }
                });
                MqTask.count++;
            }
            if(log.isDebugEnabled()){
                log.debug("库存不足，无法继续秒杀！");
            }
        }
    }
}
