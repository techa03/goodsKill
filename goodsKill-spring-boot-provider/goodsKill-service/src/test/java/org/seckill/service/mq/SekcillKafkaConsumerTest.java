package org.seckill.service.mq;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.seckill.service.inner.SeckillExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SekcillKafkaConsumerTest {
    @InjectMocks
    private SekcillKafkaConsumer sekcillKafkaConsumer;
    @Mock
    private SeckillExecutor seckillExecutor;

    @Test
    public void onMessage() {
        sekcillKafkaConsumer.onMessage(new ConsumerRecord("1", 1, 0L, "key", "1"));
        verify(seckillExecutor, only()).dealSeckill(anyLong(),any(),any());
    }

}