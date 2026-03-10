package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.service.inner.SeckillExecutor;
import com.goodskill.service.mock.strategy.impl.RedissonStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static com.goodskill.core.enums.SeckillSolutionEnum.REDISSON_LOCK;
import static org.mockito.Mockito.*;

/**
 * RedissonStrategy 单元测试
 * 测试基于Redisson分布式锁的秒杀策略
 */
@ExtendWith(MockitoExtension.class)
class RedissonStrategyTest {

    @InjectMocks
    private RedissonStrategy redissonStrategy;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private SeckillExecutor seckillExecutor;

    @Mock
    private RLock rLock;

    /**
     * 测试：获取锁成功后执行秒杀
     */
    @Test
    void shouldExecuteSeckillWhenLockAcquired() {
        // Given
        long seckillId = 1L;
        String phoneNumber = "13800138000";
        String taskId = "task-001";
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber(phoneNumber);
        requestDto.setTaskId(taskId);

        when(redissonClient.getLock(String.valueOf(seckillId))).thenReturn(rLock);

        // When
        redissonStrategy.execute(requestDto);

        // Then
        verify(redissonClient).getLock(String.valueOf(seckillId));
        verify(rLock).lock();
        verify(seckillExecutor).dealSeckill(seckillId, phoneNumber, REDISSON_LOCK.getName(), taskId);
        verify(rLock).unlock();
    }

    /**
     * 测试：执行完成后释放锁
     */
    @Test
    void shouldReleaseLockAfterExecution() {
        // Given
        long seckillId = 2L;
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber("13800138001");
        requestDto.setTaskId("task-002");

        when(redissonClient.getLock(String.valueOf(seckillId))).thenReturn(rLock);

        // When
        redissonStrategy.execute(requestDto);

        // Then
        verify(rLock).lock();
        verify(rLock).unlock();
    }

    /**
     * 测试：秒杀执行异常时仍释放锁
     */
    @Test
    void shouldReleaseLockWhenExceptionThrown() {
        // Given
        long seckillId = 3L;
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber("13800138002");
        requestDto.setTaskId("task-003");

        when(redissonClient.getLock(String.valueOf(seckillId))).thenReturn(rLock);
        doThrow(new RuntimeException("秒杀执行失败")).when(seckillExecutor)
                .dealSeckill(seckillId, "13800138002", REDISSON_LOCK.getName(), "task-003");

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            redissonStrategy.execute(requestDto);
        });

        // 验证即使异常也会释放锁
        verify(rLock).unlock();
    }
}
