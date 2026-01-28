package com.goodskill.service.handler;

import com.goodskill.core.constant.SeckillStatusConstant;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisPreRequestHandlerTest {
    @Mock
    private RedisService redisService;

    @InjectMocks
    private RedisPreRequestHandler handler;

    private SeckillWebMockRequestDTO request;
    private Seckill seckill;

    @BeforeEach
    void setUp() {
        request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(100);
        request.setTaskId("task1");

        seckill = new Seckill();
        seckill.setSeckillId(1000L);
        seckill.setNumber(100);
        seckill.setStatus(SeckillStatusConstant.WAIT);
    }

    @Test
    void testHandle() {
        when(redisService.getSeckill(1000L)).thenReturn(seckill);

        handler.handle(request);

        verify(redisService, times(1)).removeSeckill(1000L);
        verify(redisService, times(1)).getSeckill(1000L);
        verify(redisService, times(1)).putSeckill(seckill);
        verify(redisService, times(1)).clearSeckillEndFlag(1000L, "task1");
    }

    @Test
    void testHandleWithDifferentSeckillId() {
        request.setSeckillId(2000L);
        request.setTaskId("task2");

        when(redisService.getSeckill(2000L)).thenReturn(seckill);

        handler.handle(request);

        verify(redisService, times(1)).removeSeckill(2000L);
        verify(redisService, times(1)).getSeckill(2000L);
        verify(redisService, times(1)).putSeckill(seckill);
        verify(redisService, times(1)).clearSeckillEndFlag(2000L, "task2");
    }

    @Test
    void testHandleUpdatesStatus() {
        when(redisService.getSeckill(1000L)).thenReturn(seckill);

        handler.handle(request);

        assertEquals(SeckillStatusConstant.IN_PROGRESS, seckill.getStatus());
        verify(redisService, times(1)).putSeckill(seckill);
    }

    @Test
    void testGetOrder() {
        int order = handler.getOrder();

        assertEquals(1, order);
    }
}
