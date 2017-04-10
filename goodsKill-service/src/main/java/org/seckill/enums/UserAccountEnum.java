package org.seckill.enums;

public enum UserAccountEnum {
	LOGIN_SUCCESS(1, "登录成功"), LOGIN_FAIL(0, "登录失败");
	private int state;
	private String stateInfo;

	UserAccountEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public int getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
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
