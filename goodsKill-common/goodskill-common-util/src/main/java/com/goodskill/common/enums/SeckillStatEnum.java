package com.goodskill.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by heng on 2016/7/16.
 */
@Getter
@AllArgsConstructor
public enum SeckillStatEnum {

	/**
	 *
	 */
	SUCCESS(1, "秒杀成功"), END(0, "秒杀结束"), REPEAT_KILL(-1, "重复秒杀"), INNER_ERROR(-2, "系统异常");
	private int state;
	private String stateInfo;

	public static SeckillStatEnum stateOf(int index) {
		for (SeckillStatEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}
}
