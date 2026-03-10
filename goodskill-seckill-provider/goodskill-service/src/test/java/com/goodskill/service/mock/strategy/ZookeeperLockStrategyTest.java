package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.service.inner.SeckillExecutor;
import com.goodskill.service.mock.strategy.impl.ZookeeperLockStrategy;
import com.goodskill.service.util.ZookeeperLockUtil;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.goodskill.core.enums.SeckillSolutionEnum.ZOOKEEPER_LOCK;
import static org.mockito.Mockito.*;

/**
 * ZookeeperLockStrategy 单元测试
 * 测试基于Zookeeper分布式锁的秒杀策略
 */
@ExtendWith(MockitoExtension.class)
class ZookeeperLockStrategyTest {

    @InjectMocks
    private ZookeeperLockStrategy zookeeperLockStrategy;

    @Mock
    private ZookeeperLockUtil zookeeperLockUtil;

    @Mock
    private SeckillExecutor seckillExecutor;

    @Mock
    private InterProcessMutex lock;

    /**
     * 测试：获取锁成功后执行秒杀
     */
    @Test
    void shouldExecuteSeckillWhenLockAcquired() throws Exception {
        // Given
        long seckillId = 1L;
        String phoneNumber = "13800138000";
        String taskId = "task-001";
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber(phoneNumber);
        requestDto.setTaskId(taskId);

        when(zookeeperLockUtil.lock(seckillId)).thenReturn(lock);

        // When
        zookeeperLockStrategy.execute(requestDto);

        // Then
        verify(zookeeperLockUtil).lock(seckillId);
        verify(seckillExecutor).dealSeckill(seckillId, phoneNumber, ZOOKEEPER_LOCK.getName(), taskId);
        verify(lock).release();
    }

    /**
     * 测试：锁为null时不执行秒杀
     */
    @Test
    void shouldNotExecuteWhenLockIsNull() throws Exception {
        // Given
        long seckillId = 2L;
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber("13800138001");
        requestDto.setTaskId("task-002");

        when(zookeeperLockUtil.lock(seckillId)).thenReturn(null);

        // When
        zookeeperLockStrategy.execute(requestDto);

        // Then
        verify(zookeeperLockUtil).lock(seckillId);
        verify(seckillExecutor, never()).dealSeckill(anyLong(), anyString(), anyString(), anyString());
        verify(lock, never()).release();
    }

    /**
     * 测试：执行异常时仍释放锁
     */
    @Test
    void shouldReleaseLockWhenExceptionThrown() throws Exception {
        // Given
        long seckillId = 3L;
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        requestDto.setSeckillId(seckillId);
        requestDto.setPhoneNumber("13800138002");
        requestDto.setTaskId("task-003");

        when(zookeeperLockUtil.lock(seckillId)).thenReturn(lock);
        doThrow(new RuntimeException("秒杀执行失败"))
                .when(seckillExecutor).dealSeckill(seckillId, "13800138002", ZOOKEEPER_LOCK.getName(), "task-003");

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            zookeeperLockStrategy.execute(requestDto);
        });

        // 验证即使异常也会释放锁
        verify(lock).release();
    }
}
