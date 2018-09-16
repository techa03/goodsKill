package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class SeckillActiveConsumer implements MessageListener {
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Autowired
    private MqTask mqTask;
    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;

    /**
     * 监听消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        log.info("收到消息{}", message.toString());
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
            mqTask.sendSeckillSuccessTopic(seckillId, "秒杀场景三(activemq消息队列实现)");
            if (log.isDebugEnabled()) {
                log.debug("库存不足，无法继续秒杀！");
            }
        }

        // 处理成功后给请求发送回复,前提是请求方要求应答
        try {
            if (message.getJMSReplyTo() != null) {
                jmsTemplate.send(message.getJMSReplyTo(), new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        Message mes = session.createMessage();
                        mes.setJMSCorrelationID(message.getJMSCorrelationID());
                        mes.setStringProperty("message", "dealSuccess!");
                        return mes;
                    }
                });
            }
        } catch (JMSException e) {
            log.warn(e.getMessage(), e);
        }
    }
}

