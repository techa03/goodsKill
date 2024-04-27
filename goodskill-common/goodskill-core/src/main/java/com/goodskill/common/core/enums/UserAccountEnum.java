package com.goodskill.common.core.enums;

import lombok.Getter;

/**
 * 用户账户登录状态枚举
 * @author heng
 */
@Getter
public enum UserAccountEnum {
	/**
	 *
	 */
	LOGIN_SUCCESS(1, "登录成功"), LOGIN_FAIL(0, "登录失败");
	private final int state;
	private final String stateInfo;

	UserAccountEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public static UserAccountEnum stateOf(int index) {
		for (UserAccountEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}
}
