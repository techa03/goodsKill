package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.common.core.enums.States;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.mock.strategy.impl.SynchronizedLockStrategy;
import com.goodskill.service.util.StateMachineUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SynchronizedLockStrategyTest {
    @InjectMocks
    private SynchronizedLockStrategy synchronizedLockStrategy;
    @Mock
    private SeckillMapper seckillMapper;
    @Mock
    private SuccessKilledMapper successKilledMapper;
    @Spy
    private ThreadPoolExecutor taskExecutor =
            new ThreadPoolExecutor(1,1,2L,
                    TimeUnit.SECONDS, new ArrayBlockingQueue(1));
    @Mock
    private StreamBridge streamBridge;
    @Mock
    private StateMachineUtil stateMachineUtil;

    @Test
    public void execute() {
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        long seckillId = 1L;
        requestDto.setSeckillId(seckillId);
        requestDto.setCount(1);
        requestDto.setPhoneNumber("1");

        Seckill seckill = new Seckill();
        seckill.setNumber(0);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        when(stateMachineUtil.checkState(seckillId, States.IN_PROGRESS)).thenReturn(true);
        synchronizedLockStrategy.execute(requestDto);

        Seckill sendTopicResult = new Seckill();
        sendTopicResult.setSeckillId(seckillId);
//        sendTopicResult.setStatus(SeckillStatusConstant.END);
        verify(seckillMapper, atLeastOnce()).selectById(seckillId);
    }

    @Test
    public void executeNumberGt0() {
        SeckillMockRequestDTO requestDto = new SeckillMockRequestDTO();
        long seckillId = 1L;
        requestDto.setSeckillId(seckillId);
        requestDto.setCount(1);
        requestDto.setPhoneNumber("1");

        Seckill seckill = new Seckill();
        seckill.setNumber(1);
        when(seckillMapper.selectById(seckillId)).thenReturn(seckill);
        synchronizedLockStrategy.execute(requestDto);

        Seckill sendTopicResult = new Seckill();
        sendTopicResult.setSeckillId(seckillId);
//        sendTopicResult.setStatus(SeckillStatusConstant.END);
        verify(seckillMapper, never()).updateById(sendTopicResult);
    }
}
