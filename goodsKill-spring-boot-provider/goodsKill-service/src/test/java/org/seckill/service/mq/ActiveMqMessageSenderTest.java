package org.seckill.service.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.api.service.SeckillService;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ActiveMqMessageSenderTest {
    @InjectMocks
    private ActiveMqMessageSender activeMqMessageSender;
    @Mock
    private JmsTemplate jmsTopicTemplate;
    @Mock
    private SeckillService seckillService;

    @Test
    public void sendSeckillSuccessTopic() {
        long seckillId = 1L;
        activeMqMessageSender.sendSeckillSuccessTopic(seckillId,"test");
        verify(jmsTopicTemplate, only()).send(any());
        verify(seckillService, only()).getSuccessKillCount(seckillId);
        assertTrue(true);
    }
}