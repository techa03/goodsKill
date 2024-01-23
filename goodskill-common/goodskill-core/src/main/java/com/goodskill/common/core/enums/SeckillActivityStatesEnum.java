package com.goodskill.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀活动状态枚举
 * @author techa03
 * @since 2024/1/20
 */
@AllArgsConstructor
@Getter
public enum SeckillActivityStatesEnum implements BaseStates {
    /**
     * 活动已创建尚未开始
     */
    INIT,
    /**
     * 活动进行中
     */
    IN_PROGRESS,
    /**
     * 活动已结束
     */
    END,
    /**
     * 活动已中断
     */
    INTERRUPTTED,
    ;

//    // 支付状态机内容
//    private static final StateMachine<SeckillActivityStatesEnum, ActivityEvent> STATE_MACHINE = new StateMachine<>();
//    static {
//        STATE_MACHINE.accept(null, ActivityEvent.ACTIVITY_CREATE, INIT);
//        STATE_MACHINE.accept(INIT, ActivityEvent.ACTIVITY_START, IN_PROGRESS);
//        STATE_MACHINE.accept(IN_PROGRESS, ActivityEvent.ACTIVITY_END, END);
//        STATE_MACHINE.accept(IN_PROGRESS, ActivityEvent.ACTIVITY_INTERRUPT, INTERRUPTTED);
//    }

}
