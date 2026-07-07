package com.goodskill.service.inner;

import com.goodskill.core.enums.Events;
import com.goodskill.core.enums.States;
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
import static org.mockito.ArgumentMatchers.eq;
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

    /**
     * 当reduceNumber返回0（扣减失败）且状态机不在IN_PROGRESS状态时，
     * 不应调用feedMachine和streamBridge
     */
    @Test
    void dealSeckillWhenReduceNumberFailsAndStateNotInProgress() {
        long seckillId = 1001L;
        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        seckill.setGoodsId(1);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(seckillService.reduceNumber(any())).thenReturn(0);
        when(stateMachineService.checkState(seckillId, States.IN_PROGRESS)).thenReturn(false);

        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", "1");

        verify(seckillMapper, times(1)).selectById(seckillId);
        verify(seckillService, times(1)).reduceNumber(any());
        verify(stateMachineService, times(1)).checkState(seckillId, States.IN_PROGRESS);
        verify(stateMachineService, never()).feedMachine(any(), anyLong());
        verify(streamBridge, never()).send(any(), any());
    }

    /**
     * 当reduceNumber返回1（扣减成功）时，不应查询库存、不应调用状态机和streamBridge
     */
    @Test
    void dealSeckillWhenReduceNumberSucceeds() {
        long seckillId = 1001L;
        when(seckillService.reduceNumber(any())).thenReturn(1);

        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", "1");

        verify(seckillService, times(1)).reduceNumber(any());
        verify(seckillMapper, never()).selectById(anyLong());
        verify(stateMachineService, never()).checkState(anyLong(), any());
        verify(streamBridge, never()).send(any(), any());
    }

    /**
     * 当扣减失败、状态机IN_PROGRESS、但结束标识已设置时（setSeckillEndFlag返回false），
     * 不应发送streamBridge通知
     */
    @Test
    void dealSeckillWhenEndFlagAlreadySet() {
        long seckillId = 1001L;
        String taskId = "task-1";
        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(seckillService.reduceNumber(any())).thenReturn(0);
        when(stateMachineService.checkState(seckillId, States.IN_PROGRESS)).thenReturn(true);
        when(redisService.setSeckillEndFlag(seckillId, taskId)).thenReturn(false);

        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", taskId);

        verify(stateMachineService, times(1)).feedMachine(Events.ACTIVITY_CALCULATE, seckillId);
        verify(redisService, times(1)).setSeckillEndFlag(seckillId, taskId);
        verify(streamBridge, never()).send(any(), any());
    }

    /**
     * 当扣减失败、状态机IN_PROGRESS、且成功设置结束标识时，
     * 应调用feedMachine并发送streamBridge通知
     */
    @Test
    void dealSeckillSendsNotificationWhenStockExhaustedAndEndFlagSet() {
        long seckillId = 1001L;
        String taskId = "task-1";
        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(seckillService.reduceNumber(any())).thenReturn(0);
        when(stateMachineService.checkState(seckillId, States.IN_PROGRESS)).thenReturn(true);
        when(redisService.setSeckillEndFlag(seckillId, taskId)).thenReturn(true);

        seckillProcedureExecutor.dealSeckill(seckillId, "123", "test", taskId);

        verify(stateMachineService, times(1)).feedMachine(eq(Events.ACTIVITY_CALCULATE), eq(seckillId));
        verify(redisService, times(1)).setSeckillEndFlag(seckillId, taskId);
        verify(streamBridge, times(1)).send(any(), any());
    }
}
