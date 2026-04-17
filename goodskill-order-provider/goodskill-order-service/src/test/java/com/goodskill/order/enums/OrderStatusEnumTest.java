package com.goodskill.order.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderStatusEnum 单元测试
 */
class OrderStatusEnumTest {

    @Test
    void shouldHaveCorrectCodeForPendingPayment() {
        assertEquals((byte) 1, OrderStatusEnum.PENDING_PAYMENT.getCode());
    }

    @Test
    void shouldHaveCorrectDescForPendingPayment() {
        assertEquals("待支付", OrderStatusEnum.PENDING_PAYMENT.getDesc());
    }

    @Test
    void shouldHaveCorrectCodeForPaid() {
        assertEquals((byte) 2, OrderStatusEnum.PAID.getCode());
    }

    @Test
    void shouldHaveCorrectDescForPaid() {
        assertEquals("已支付", OrderStatusEnum.PAID.getDesc());
    }

    @Test
    void shouldHaveCorrectCodeForCancelled() {
        assertEquals((byte) 3, OrderStatusEnum.CANCELLED.getCode());
    }

    @Test
    void shouldHaveCorrectDescForCancelled() {
        assertEquals("已取消", OrderStatusEnum.CANCELLED.getDesc());
    }

    @Test
    void shouldGetByCodeReturnCorrectEnum() {
        assertEquals(OrderStatusEnum.PENDING_PAYMENT, OrderStatusEnum.getByCode((byte) 1));
        assertEquals(OrderStatusEnum.PAID, OrderStatusEnum.getByCode((byte) 2));
        assertEquals(OrderStatusEnum.CANCELLED, OrderStatusEnum.getByCode((byte) 3));
    }

    @Test
    void shouldGetByCodeReturnNullForInvalidCode() {
        assertNull(OrderStatusEnum.getByCode((byte) 99));
    }

    @Test
    void shouldGetByCodeReturnNullForNullCode() {
        assertNull(OrderStatusEnum.getByCode(null));
    }

    @Test
    void shouldHaveThreeValues() {
        assertEquals(3, OrderStatusEnum.values().length);
    }
}
