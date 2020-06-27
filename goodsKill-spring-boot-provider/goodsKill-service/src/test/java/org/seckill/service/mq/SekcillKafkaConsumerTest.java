package org.seckill.service.mq;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.service.common.RedisService;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.jms.Message;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SekcillKafkaConsumerTest {
    @InjectMocks
    private SekcillKafkaConsumer sekcillKafkaConsumer;
    @Mock
    private RedisService redisService;
    @Mock
    private RedisTemplate redisTemplate;
    @Mock
    private SeckillMapper seckillMapper;
    @Mock
    private ThreadPoolExecutor taskExecutor;
    @Mock
    private SeckillExecutor seckillExecutor;
    @Mock
    private ActiveMqMessageSender activeMqMessageSender;

    @Test
    public void onMessage() {
        sekcillKafkaConsumer.onMessage(new ConsumerRecord("1", 1, 0L, "key", "1"));
        verify(seckillExecutor, only()).dealSeckill(anyLong(),any(),any());
    }

    @Test
    public void processMessage() {
        ValueOperations valueOperations = mock(ValueOperations.class);
        Seckill value = new Seckill();
        value.setNumber(1);
        when(redisService.getSeckill(anyLong())).thenReturn(value);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(any())).thenReturn(1L);
        Message message = mock(Message.class);
        sekcillKafkaConsumer.processMessage(message);
        verify(activeMqMessageSender, only()).sendSeckillSuccessTopic(anyLong(), isNull());
        verify(seckillMapper, only()).updateById(any());
    }

    @Test
    public void processMessage1() {
        ValueOperations valueOperations = mock(ValueOperations.class);
        Seckill value = new Seckill();
        value.setNumber(1);
        when(redisService.getSeckill(anyLong())).thenReturn(value);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(any())).thenReturn(-1L);
        Message message = mock(Message.class);
        sekcillKafkaConsumer.processMessage(message);
        verify(taskExecutor, only()).execute(any());
    }
}