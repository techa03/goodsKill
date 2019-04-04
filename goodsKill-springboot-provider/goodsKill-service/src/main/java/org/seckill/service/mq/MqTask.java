package org.seckill.service.mq;

import org.seckill.api.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class MqTask {
    @Autowired
    @Qualifier("jmsTopicTemplate")
    private JmsTemplate jmsTopicTemplate;
    @Autowired
    private SeckillService seckillService;

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
