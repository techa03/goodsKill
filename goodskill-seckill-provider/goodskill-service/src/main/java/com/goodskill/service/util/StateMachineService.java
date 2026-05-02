package com.goodskill.service.util;

import com.goodskill.core.enums.Events;
import com.goodskill.core.enums.States;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StateMachineService implements CommandLineRunner {
    public static final String STATEMACHINE_REDIS_KEY_PREFIX = "seckillId:";

    private final Map<String, StateMachine<States, Events>> stateMachineMap = new ConcurrentHashMap<>();
    @Resource
    private StateMachineFactory<States, Events> stateMachineFactory;
    @Resource
    private StateMachinePersister<States, Events, String> stateMachinePersister;

    /**
     * 向状态机中输入事件,用于处理活动事件的方法
     *
     * @param e           待处理的活动事件
     */
    @SneakyThrows
    public boolean feedMachine(Events e, long seckillId) {
        log.info("feedMachine 开始, seckillId: {}, 事件: {}", seckillId, e);
        StateMachine<States, Events> stateMachine = stateMachineMap.get(String.valueOf(seckillId));
        if (stateMachine == null) {
            log.error("feedMachine 失败, 状态机不存在, seckillId: {}", seckillId);
            return false;
        }
        States beforeState = stateMachine.getState().getId();
        log.info("feedMachine restore前, seckillId: {}, 事件: {}, 内存状态: {}", seckillId, e, beforeState);

        stateMachinePersister.restore(stateMachine, STATEMACHINE_REDIS_KEY_PREFIX + seckillId);
        States afterRestoreState = stateMachine.getState().getId();
        log.info("feedMachine restore后, seckillId: {}, 事件: {}, 状态: {} -> {}", seckillId, e, beforeState, afterRestoreState);
        StateMachineEventResult<States, Events> eventResult =
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(e).build())).blockLast();
        States afterSendState = stateMachine.getState().getId();
        log.info("feedMachine sendEvent后, seckillId: {}, 事件: {}, 状态: {}, 结果: {}",
                seckillId, e, afterSendState, eventResult != null ? eventResult.getResultType() : "null");

        stateMachinePersister.persist(stateMachine, STATEMACHINE_REDIS_KEY_PREFIX + seckillId);
        log.info("feedMachine persist完成, seckillId: {}, 事件: {}, 最终状态: {}", seckillId, e, afterSendState);

        boolean accepted = eventResult != null && StateMachineEventResult.ResultType.ACCEPTED.equals(eventResult.getResultType());
        log.info("feedMachine 结束, seckillId: {}, 事件: {}, 是否接受: {}", seckillId, e, accepted);
        return accepted;
    }


    /**
     * 检查秒杀活动的状态是否符合要求
     *
     * @param seckillId 秒杀活动的ID
     * @param state     指定的状态
     * @return 如果当前状态与state相同则返回true,否则返回false
     */
    @SneakyThrows
    public boolean checkState(long seckillId, States state) {
        log.info("checkState 开始, seckillId: {}, 期望状态: {}", seckillId, state);
        StateMachine<States, Events> stateMachine = stateMachineMap.get(String.valueOf(seckillId));
        if (stateMachine == null) {
            log.error("checkState 失败, 状态机不存在, seckillId: {}, 期望状态: {}", seckillId, state);
            return false;
        }

        States beforeState = stateMachine.getState().getId();
        log.info("checkState restore前, seckillId: {}, 期望状态: {}, 内存状态: {}", seckillId, state, beforeState);

        stateMachinePersister.restore(stateMachine, STATEMACHINE_REDIS_KEY_PREFIX + seckillId);

        States afterRestoreState = stateMachine.getState().getId();
        log.info("checkState restore后, seckillId: {}, 期望状态: {}, 状态: {} -> {}",
                seckillId, state, beforeState, afterRestoreState);

        boolean result = afterRestoreState.equals(state);
        log.info("checkState 结束, seckillId: {}, 期望状态: {}, 实际状态: {}, 结果: {}",
                seckillId, state, afterRestoreState, result);
        return result;
    }

    @SneakyThrows
    public StateMachine<States, Events> initStateMachine(long seckillId) {
        log.info("initStateMachine 开始, seckillId: {}", seckillId);
        String key = String.valueOf(seckillId);
        StateMachine<States, Events> existing = stateMachineMap.get(key);
        if (existing != null) {
            log.warn("initStateMachine 跳过, 状态机已存在, seckillId: {}, 当前状态: {}", seckillId, existing.getState().getId());
            return existing;
        }
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
        log.info("initStateMachine 创建新实例, seckillId: {}, 初始状态: {}", seckillId, stateMachine.getState().getId());
        stateMachinePersister.persist(stateMachine, STATEMACHINE_REDIS_KEY_PREFIX + seckillId);
        log.info("initStateMachine persist完成, seckillId: {}, 状态: {}", seckillId, stateMachine.getState().getId());
        StateMachine<States, Events> previous = stateMachineMap.put(key, stateMachine);
        log.info("initStateMachine 放入map完成, seckillId: {}, 之前是否存在: {}, 当前状态: {}", seckillId, previous != null, stateMachine.getState().getId());
        return previous;
    }

    @Override
    public void run(String... args) throws Exception {
        initStateMachine(1001L);
        feedMachine(Events.ACTIVITY_START, 1001L);
        boolean b = checkState(1001L, States.IN_PROGRESS);
        System.out.println(b);
    }
}
