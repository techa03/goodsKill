package org.seckill.service.mq;

import org.seckill.api.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

/**
 * ActiveMq消息发送者
 *
 * @author heng
 */
@Component
public class ActiveMqMessageSender {
    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTopicTemplate;
    @Autowired
    private SeckillService seckillService;

    /**
     * 发送秒杀成功通知，会在最后一次秒杀成功后发出
     *
     * @param seckillId 秒杀活动id
     * @param note 通知内容
     */
    public void sendSeckillSuccessTopic(long seckillId, String note) {
        seckillService.getSuccessKillCount(seckillId);
        long finalSeckillId = seckillId;
        jmsTopicTemplate.send(session -> {
            Message message = session.createMessage();
            message.setLongProperty("seckillId", finalSeckillId);
            message.setBooleanProperty("status", true);
            message.setStringProperty("note", note);
            return message;
        });
    }

}
