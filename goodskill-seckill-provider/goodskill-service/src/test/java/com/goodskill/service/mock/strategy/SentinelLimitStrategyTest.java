package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.service.inner.SeckillExecutor;
import com.goodskill.service.mock.strategy.impl.SentinelLimitStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.goodskill.core.enums.SeckillSolutionEnum.ATOMIC_UPDATE;
import static org.mockito.Mockito.*;

/**
 * SentinelLimitStrategy 单元测试
 * 测试基于Sentinel限流的秒杀策略
 */
@ExtendWith(MockitoExtension.class)
class SentinelLimitStrategyTest {

    @InjectMocks
    private SentinelLimitStrategy sentinelLimitStrategy;

    @Mock
    private SeckillExecutor seckillExecutor;

    /**
     * 测试：正常执行秒杀（未触发限流）
     */
    @Test
    void shouldExecuteSeckillWhenNotLimited() {
        // Given
        long seckillId = 1L;
        String phoneNumber = "13800138000";
        String taskId = "task-001";
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber(phoneNumber);
        requestDto.setTaskId(taskId);

        // When
        sentinelLimitStrategy.execute(requestDto);

        // Then
        verify(seckillExecutor).dealSeckill(seckillId, phoneNumber, ATOMIC_UPDATE.getName(), taskId);
    }

    /**
     * 测试：参数正确传递
     */
    @Test
    void shouldPassCorrectParametersToExecutor() {
        // Given
        long seckillId = 2L;
        String phoneNumber = "13900139000";
        String taskId = "task-002";
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber(phoneNumber);
        requestDto.setTaskId(taskId);

        // When
        sentinelLimitStrategy.execute(requestDto);

        // Then
        verify(seckillExecutor, times(1)).dealSeckill(
                eq(seckillId),
                eq(phoneNumber),
                eq(ATOMIC_UPDATE.getName()),
                eq(taskId)
        );
    }

    /**
     * 测试：执行器异常时正常抛出
     * 注意：实际限流行为由Sentinel框架控制，单元测试中不模拟限流场景
     */
    @Test
    void shouldPropagateExceptionWhenExecutorFails() {
        // Given
        long seckillId = 3L;
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber("13800138002");
        requestDto.setTaskId("task-003");

        doThrow(new RuntimeException("秒杀执行失败"))
                .when(seckillExecutor).dealSeckill(anyLong(), anyString(), anyString(), anyString());

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            sentinelLimitStrategy.execute(requestDto);
        });

        verify(seckillExecutor).dealSeckill(seckillId, "13800138002", ATOMIC_UPDATE.getName(), "task-003");
    }
}
