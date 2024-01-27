package com.goodskill.service.util;

import com.goodskill.common.core.enums.Events;
import com.goodskill.common.core.enums.States;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StateMachineUtil {
    @Resource
    private StateMachine<States, Events> stateMachine;
    @Resource
    private StateMachinePersister<States, Events, String> stateMachinePersister;

    /**
     * 向状态机中输入事件，用于处理活动事件的方法
     *
     * @param e           待处理的活动事件
     */
    @SneakyThrows
    public boolean feedMachine(Events e, long seckillId) {
        stateMachinePersister.restore(stateMachine, "seckillId:" + seckillId);
        StateMachineEventResult<States, Events> eventResult =
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(e).build())).blockLast();
        log.info("eventResult:{}", eventResult);
        stateMachinePersister.persist(stateMachine, "seckillId:" + seckillId);
        if (eventResult != null && StateMachineEventResult.ResultType.ACCEPTED.equals(eventResult.getResultType())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 检查秒杀活动的状态是否符合要求
     *
     * @param seckillId 秒杀活动的ID
     * @param state     指定的状态
     * @return 如果当前状态与state相同则返回true，否则返回false
     */
    @SneakyThrows
    public boolean checkState(long seckillId, States state) {
        stateMachinePersister.restore(stateMachine, "seckillId:" + seckillId);
        return stateMachine.getState().getId().equals(state);
    }

}
