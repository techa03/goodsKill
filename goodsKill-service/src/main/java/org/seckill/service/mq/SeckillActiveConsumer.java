package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

@Slf4j
public class SeckillActiveConsumer implements MessageListener {
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;
    @Autowired
    private SeckillExecutor seckillExecutor;

    /**
     * 监听消息
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        log.info("收到消息{}", message.toString());
        long seckillId = 0;
        String userPhone = null;
        String note = null;
        try {
            seckillId = message.getLongProperty("seckillId");
            userPhone = message.getStringProperty("userPhone");
            note = message.getStringProperty("note");
        } catch (JMSException e) {
            log.error(e.getMessage(), e);
        }
        seckillExecutor.dealSeckill(seckillId, userPhone, note);

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

