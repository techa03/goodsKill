package com.goodskill.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付事件
 */
@Getter
@AllArgsConstructor
public enum Events {
    // 活动创建
    ACTIVITY_CREATE,
    // 活动开始
    ACTIVITY_START,
    // 活动开始结算
    ACTIVITY_CALCULATE,
    // 活动结束
    ACTIVITY_END,
    // 中止活动
    ACTIVITY_INTERRUPT,
    // 重置
    ACTIVITY_RESET,
    ;

}
