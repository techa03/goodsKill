package com.goodskill.service.config;

import com.goodskill.common.core.enums.Events;
import com.goodskill.common.core.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.data.redis.RedisStateMachinePersister;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static com.goodskill.common.core.enums.Events.*;
import static com.goodskill.common.core.enums.States.*;

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
        // 状态初始化
        states
            .withStates()
                .initial(INIT)
                    .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        // 控制流程状态跳转
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
                log.info("State change to {}", to.getId());
            }

            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.info("Event not accepted {}", event.getPayload());
                super.eventNotAccepted(event);
            }
        };
    }

    @Bean
    public StateMachinePersist<States, Events, String> stateMachinePersist(RedisConnectionFactory connectionFactory) {
        RedisStateMachineContextRepository<States, Events> repository =
                new RedisStateMachineContextRepository<>(connectionFactory);
        return new RepositoryStateMachinePersist<>(repository);
    }

    @Bean
    public RedisStateMachinePersister<States, Events> redisStateMachinePersister(
            StateMachinePersist<States, Events, String> stateMachinePersist) {
        return new RedisStateMachinePersister<>(stateMachinePersist);
    }
}

