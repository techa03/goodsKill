package com.goodskill.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class CommonConstants {
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

        private int code;
        private String desc;

    }

}
