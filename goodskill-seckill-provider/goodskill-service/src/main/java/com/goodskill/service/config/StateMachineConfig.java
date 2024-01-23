package com.goodskill.service.config;

import com.goodskill.common.core.enums.ActivityEvent;
import com.goodskill.common.core.enums.SeckillActivityStatesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

import static com.goodskill.common.core.enums.ActivityEvent.*;
import static com.goodskill.common.core.enums.SeckillActivityStatesEnum.*;

/**
 * 控制秒杀活动的状态机配置
 * @author techa03
 * @since 2024/1/20
 */
@Configuration
@EnableStateMachine
@Slf4j
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<SeckillActivityStatesEnum, ActivityEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<SeckillActivityStatesEnum, ActivityEvent> config)
            throws Exception {
        config
            .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<SeckillActivityStatesEnum, ActivityEvent> states)
            throws Exception {
        // 状态初始化
        states
            .withStates()
                .initial(INIT)
                    .states(EnumSet.allOf(SeckillActivityStatesEnum.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SeckillActivityStatesEnum, ActivityEvent> transitions)
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
                .source(IN_PROGRESS).target(END).event(ACTIVITY_END)
                .and()
            .withExternal()
                .source(IN_PROGRESS).target(INTERRUPTTED).event(ACTIVITY_INTERRUPT)
        ;
    }

    @Bean
    public StateMachineListener<SeckillActivityStatesEnum, ActivityEvent> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<SeckillActivityStatesEnum, ActivityEvent> from, State<SeckillActivityStatesEnum, ActivityEvent> to) {
                log.info("State change to {}", to.getId());
            }
        };
    }
}

