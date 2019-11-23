package org.seckill.service.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class SeckillActiveConsumerTest {
    @InjectMocks
    private SeckillActiveConsumer seckillActiveConsumer;
    @Mock
    private SeckillExecutor seckillExecutor;
    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void onMessage() throws JMSException {
        Message message = mock(Message.class);
        Destination destination = mock(Destination.class);
        when(message.getJMSReplyTo()).thenReturn(destination);
        seckillActiveConsumer.onMessage(message);
        verify(seckillExecutor, times(1)).dealSeckill(eq(0L), isNull(), isNull());
        verify(jmsTemplate, atLeastOnce()).send(eq(destination), isNotNull());
    }
}