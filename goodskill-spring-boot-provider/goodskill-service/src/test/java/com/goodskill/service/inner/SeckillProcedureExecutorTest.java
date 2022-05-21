package com.goodskill.service.inner;

import com.goodskill.api.service.SeckillService;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.entity.Seckill;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.service.common.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

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

    @Test
    void dealSeckill() {
        long seckillId = 1001L;
        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(redisService.setSeckillEndFlag(seckillId, "1")).thenReturn(true);
        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", "1");
        Seckill updateSeckill = new Seckill();
        updateSeckill.setSeckillId(seckillId);
        updateSeckill.setStatus(SeckillStatusConstant.END);
        verify(seckillMapper, times(1)).updateById(updateSeckill);
    }
}