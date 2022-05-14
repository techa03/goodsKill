package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.entity.Seckill;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.service.common.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

class RedisMongoReactiveStrategyTest {
    @Mock
    RedisService redisService;
    @Mock
    RedisTemplate redisTemplate;
    @Mock
    ValueOperations redisOperations;
    @Spy
    ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(1,1,10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    @Mock
    SeckillMapper extSeckillMapper;
    @Mock
    StreamBridge streamBridge;
    @Mock
    Logger log;
    @InjectMocks
    RedisMongoReactiveStrategy redisMongoReactiveStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() {
        Seckill t = new Seckill();
        t.setNumber(10);
        when(redisService.getSeckill(anyLong())).thenReturn(t);
        when(redisTemplate.opsForValue()).thenReturn(redisOperations);
        when(redisOperations.increment(anyLong())).thenReturn(1L);
        redisMongoReactiveStrategy.execute(new SeckillMockRequestDTO(0L, 0, "phoneNumber", "requestTime"));
        assertNotNull(t);
    }

    @Test
    void testExecute2() {
        Seckill t = new Seckill();
        t.setNumber(1);
        when(redisService.getSeckill(anyLong())).thenReturn(t);
        when(redisTemplate.opsForValue()).thenReturn(redisOperations);
        when(redisOperations.increment(anyLong())).thenReturn(2L);
        redisMongoReactiveStrategy.execute(new SeckillMockRequestDTO(0L, 0, "phoneNumber", "requestTime"));
        assertNotNull(t);
    }
}
