package com.goodskill.service.handler;

import com.goodskill.core.enums.Events;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.service.util.StateMachineService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class StateMachinePreRequestHandler extends AbstractPreRequestHandler {
    @Resource
    private StateMachineService stateMachineService;

    @Override
    public void handle(SeckillWebMockRequestDTO request) {
        // 状态机初始化
        stateMachineService.initStateMachine(request.getSeckillId());
        // 初始化库存数量
        // 使用状态机控制活动状态
        if (!stateMachineService.feedMachine(Events.ACTIVITY_RESET, request.getSeckillId())) {
            throw new RuntimeException("活动尚未结束，请等待活动结束后再次操作");
        }
        stateMachineService.feedMachine(Events.ACTIVITY_START, request.getSeckillId());
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
