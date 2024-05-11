package com.goodskill.service.mock.strategy.impl;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.common.core.enums.Events;
import com.goodskill.common.core.enums.States;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.util.StateMachineService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static com.goodskill.common.core.enums.SeckillSolutionEnum.SYCHRONIZED;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class SynchronizedLockStrategy implements GoodsKillStrategy {
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private StreamBridge streamBridge;
    @Resource
    private StateMachineService stateMachineService;

    private final ConcurrentHashMap<Long, Object> seckillIdList = new ConcurrentHashMap<>();

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        Long seckillId = requestDto.getSeckillId();
        String userId = requestDto.getPhoneNumber();
        // 此处已优化，锁单个商品而不是整个方法
        Object seckillMonitor = seckillIdList.get(seckillId);
        if (seckillMonitor == null) {
            Object value = new Object();
            seckillIdList.put(seckillId, value);
        }
        synchronized (seckillIdList.get(seckillId)) {
            Seckill seckill = seckillMapper.selectById(seckillId);
            if (seckill.getNumber() > 0) {
                seckillMapper.reduceNumber(seckillId, new Date());
                SuccessKilled record = new SuccessKilled();
                record.setSeckillId(seckillId);
                record.setUserPhone(String.valueOf(userId));
                record.setStatus(1);
                record.setCreateTime(new Date());
                successKilledMapper.insert(record);
            } else {
                if (stateMachineService.checkState(seckillId, States.IN_PROGRESS)) {
                    stateMachineService.feedMachine(Events.ACTIVITY_CALCULATE, seckillId);
                    streamBridge.send(DEFAULT_BINDING_NAME, MessageBuilder.withPayload(
                                    SeckillMockResponseDTO
                                            .builder()
                                            .seckillId(seckillId)
                                            .status(true)
                                            .note(SYCHRONIZED.getName())
                                            .taskId(requestDto.getTaskId())
                                            .build())
                            .build());
                }
                if (log.isDebugEnabled()) {
                    log.debug("库存不足，无法继续秒杀！");
                }
            }
        }
    }
}
