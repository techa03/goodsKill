package com.goodskill.service.config;

import com.goodskill.core.enums.Events;
import com.goodskill.core.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.goodskill.core.enums.Events.*;
import static com.goodskill.core.enums.States.*;

/**
 * 控制秒杀活动的状态机配置
 * @author techa03
 * @since 2024/1/20
 */
@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
            .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
            .withStates()
                .initial(INIT)
                    .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
            .withExternal()
                .source(INIT).target(INIT).event(ACTIVITY_CREATE)
                .and()
            .withExternal()
                .source(INIT).target(IN_PROGRESS).event(ACTIVITY_START)
                .and()
            .withExternal()
                .source(IN_PROGRESS).target(CALCULATING).event(ACTIVITY_CALCULATE)
                .and()
            .withExternal()
                .source(CALCULATING).target(END).event(ACTIVITY_END)
                .and()
            .withExternal()
                .source(IN_PROGRESS).target(END).event(ACTIVITY_END)
                .and()
            .withExternal()
                .source(IN_PROGRESS).target(INTERRUPTTED).event(ACTIVITY_INTERRUPT)
            .and()
            .withExternal()
                .source(END).target(INIT).event(ACTIVITY_RESET)
                .and()
            .withExternal()
                .source(INTERRUPTTED).target(INIT).event(ACTIVITY_RESET)
                .and()
                .withExternal()
                .source(INIT).target(INIT).event(ACTIVITY_RESET);
    }

    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                log.info("状态机状态已变更为： {}, 原状态：{}", to.getId(), from != null ? from.getId() : null);
            }

            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.info("Event not accepted {}", event.getPayload());
                super.eventNotAccepted(event);
            }
        };
    }

    @Bean
    public StateMachinePersister<States, Events, String> stateMachinePersister() {
        return new StateMachinePersister<>() {
            private final Map<String, StateMachine<States, Events>> cache = new HashMap<>();

            @Override
            public void persist(StateMachine<States, Events> sm, String s) {
                cache.put(s, sm);
            }

            @Override
            public StateMachine<States, Events> restore(StateMachine<States, Events> sm, String s) {
                StateMachine<States, Events> cached = cache.get(s);
                if (cached != null) {
                    return cached;
                }
                return sm;
            }
        };
    }
}

