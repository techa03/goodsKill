package com.goodskill.service.mq;

import com.goodskill.service.inner.SeckillExecutor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SeckillKafkaConsumerTest {
    @InjectMocks
    private SeckillKafkaConsumer seckillKafkaConsumer;
    @Mock
    private SeckillExecutor seckillExecutor;

    @Test
    public void onMessage() {
        seckillKafkaConsumer.onMessage(new ConsumerRecord("1", 1, 0L, "key", "1"));
        verify(seckillExecutor, only()).dealSeckill(anyLong(),any(),any());
    }

}