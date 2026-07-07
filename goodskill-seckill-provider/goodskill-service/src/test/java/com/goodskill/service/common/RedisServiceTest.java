package com.goodskill.service.common;

import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mapper.SeckillMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    @InjectMocks
    private RedisService redisService;
    @Mock
    private SeckillMapper seckillMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void getSeckillError() {
        long seckillId = 1001L;
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        assertThrows(RuntimeException.class, () -> redisService.getSeckill(seckillId));
    }

    @Test
    void getSeckillSuccess() {
        long seckillId = 1001L;
        String key = "seckill:" + seckillId;
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Seckill t = new Seckill();
        t.setSeckillId(seckillId);
        when(valueOperations.get(key)).thenReturn(t);
        assertEquals((long) redisService.getSeckill(seckillId).getSeckillId(), seckillId);
    }

    @Test
    void getSeckillSuccessCache() {
        long seckillId = 1001L;
        String key = "seckill:" + seckillId;
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(seckillMapper.selectById(seckillId)).thenReturn(new Seckill());

        assertNotNull(redisService.getSeckill(seckillId));
    }

    @Test
    void putSeckill() {
        long seckillId = 1001L;
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        assertDoesNotThrow(() -> redisService.putSeckill(seckill));
    }

    @Test
    void removeSeckill() {
        assertDoesNotThrow(() -> redisService.removeSeckill(1001L));
    }

    @Test
    void testSetSeckillEndFlag() {
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Boolean result = redisService.setSeckillEndFlag(0L, "1");
        Assertions.assertEquals(Boolean.FALSE, result);
    }

    @Test
    void testSetSeckillEndFlagReturnsTrue() {
        long seckillId = 1001L;
        String taskId = "task-1";
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), eq("1"))).thenReturn(true);

        Boolean result = redisService.setSeckillEndFlag(seckillId, taskId);

        Assertions.assertTrue(result);
        verify(valueOperations, times(1)).setIfAbsent(anyString(), eq("1"));
    }

    @Test
    void testClearSeckillEndFlag() {
        Boolean result = redisService.clearSeckillEndFlag(0L, "1");
        Assertions.assertEquals(Boolean.FALSE, result);
    }

    @BeforeAll
    public static void init() {
        long seckillId = 1001L;
        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
    }

}
