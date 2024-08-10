package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisPreRequestHandlerTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RedisPreRequestHandler redisPreRequestHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should handle request and update seckill status")
    public void shouldHandleRequestAndUpdateSeckillStatus() {
        SeckillWebMockRequestDTO request = new SeckillWebMockRequestDTO();
        request.setSeckillId(123L);
        Seckill seckill = new Seckill();
        when(redisService.getSeckill(anyLong())).thenReturn(seckill);

        redisPreRequestHandler.handle(request);

        verify(redisService).removeSeckill(anyLong());
        verify(redisService).putSeckill(seckill);
        verify(redisService).clearSeckillEndFlag(anyLong(), eq(request.getTaskId()));
    }

}
