package com.goodskill.service.mq;

import com.alibaba.fastjson2.JSON;
import com.goodskill.api.dto.SeckillMockRequestDTO;
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
        SeckillMockRequestDTO seckillMockRequestDTO = new SeckillMockRequestDTO();
        seckillMockRequestDTO.setSeckillId(1001L);
        seckillKafkaConsumer.onMessage(new ConsumerRecord("1", 1, 0L, "key", JSON.toJSONString(seckillMockRequestDTO)));
        verify(seckillExecutor, only()).dealSeckill(anyLong(),any(),any(), any());
    }

}