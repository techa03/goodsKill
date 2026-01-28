package com.goodskill.service.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabasePreRequestHandlerTest {
    @Mock
    private SeckillMapper seckillMapper;

    @Mock
    private SuccessKilledMapper successKilledMapper;

    @InjectMocks
    private DatabasePreRequestHandler handler;

    private SeckillWebMockRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(100);
        request.setTaskId("task1");
    }

    @Test
    void testHandle() {
        handler.handle(request);

        verify(seckillMapper, times(1)).updateById(any(Seckill.class));
        verify(successKilledMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testHandleWithDifferentSeckillId() {
        request.setSeckillId(2000L);
        request.setSeckillCount(200);

        handler.handle(request);

        verify(seckillMapper, times(1)).updateById(any(Seckill.class));
        verify(successKilledMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testHandleWithZeroCount() {
        request.setSeckillCount(0);

        handler.handle(request);

        verify(seckillMapper, times(1)).updateById(any(Seckill.class));
        verify(successKilledMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testGetOrder() {
        int order = handler.getOrder();

        assertEquals(0, order);
    }
}
