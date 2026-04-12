package com.goodskill.service.inner;

import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.util.StateMachineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeckillProcedureExecutorTest {
    @InjectMocks
    private SeckillProcedureExecutor seckillProcedureExecutor;
    @Mock
    private SeckillMapper seckillMapper;
    @Mock
    private SeckillService seckillService;
    @Mock
    private StreamBridge streamBridge;
    @Mock
    private RedisService redisService;
    @Mock
    private StateMachineService stateMachineService;

    @Test
    void dealSeckill() {
        long seckillId = 1001L;
        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        seckill.setGoodsId(1);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(seckillService.reduceNumber(any())).thenReturn(0);
        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", "1");
        verify(seckillMapper, times(1)).selectById(seckillId);
        verify(seckillService, times(1)).reduceNumber(any());
    }
}
