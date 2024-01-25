package com.goodskill.service.util;

import com.goodskill.common.core.enums.ActivityEvent;
import com.goodskill.common.core.enums.SeckillActivityStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Mono;

@Slf4j
public class StateMachineUtil {
    /**
     * 向状态机中输入事件，用于处理活动事件的方法
     *
     * @param stateMachine 待处理的状态机
     * @param e           待处理的活动事件
     */
    public static boolean feedMachine(StateMachine<SeckillActivityStates, ActivityEvent> stateMachine, ActivityEvent e) {
        StateMachineEventResult<SeckillActivityStates, ActivityEvent> eventResult =
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(e).build())).blockLast();
        log.info("eventResult:{}", eventResult);
        if (eventResult != null && StateMachineEventResult.ResultType.ACCEPTED.equals(eventResult.getResultType())) {
            return true;
        } else {
            return false;
        }
    }
}
