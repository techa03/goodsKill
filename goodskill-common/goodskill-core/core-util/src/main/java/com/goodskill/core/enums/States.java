package com.goodskill.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀活动状态枚举
 * @author techa03
 * @since 2024/1/20
 */
@AllArgsConstructor
@Getter
public enum States {
    /**
     * 活动已创建尚未开始
     */
    INIT,
    /**
     * 活动进行中
     */
    IN_PROGRESS,
    /**
     * 活动结束结算中（统计成功交易笔数）,
     */
    CALCULATING,
    /**
     * 活动已结束
     */
    END,
    /**
     * 活动已中断
     */
    INTERRUPTTED,


}
