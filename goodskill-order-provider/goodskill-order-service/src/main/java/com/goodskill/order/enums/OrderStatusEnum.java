package com.goodskill.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    
    PENDING_PAYMENT((byte) 1, "待支付"),
    PAID((byte) 2, "已支付"),
    CANCELLED((byte) 3, "已取消");
    
    private final Byte code;
    private final String desc;
    
    OrderStatusEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static OrderStatusEnum getByCode(Byte code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}