package com.goodskill.core.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class CommonConstants {

    public static final String USER_ID_HEADER = "X-User-Id";

    private CommonConstants() {
    }

    /**
     * 用户账户状态
     */
    @Getter
    @AllArgsConstructor
    public enum UserAccountStatusEnum {

        /**
         *
         */
        NORMAL(0, "正常"),  LOCKED(1, "已冻结"),  INVALID(3, "已注销");

        private final int code;
        private final String desc;

    }

}
