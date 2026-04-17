package com.goodskill.order.controller;

import com.goodskill.core.info.Result;
import com.goodskill.order.entity.Order;
import com.goodskill.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AdminOrderController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @InjectMocks
    private AdminOrderController adminOrderController;

    @Mock
    private OrderServiceImpl orderService;

    @Test
    void shouldListOrdersWithDefaultParams() {
        // Given
        Order order = new Order();
        order.setId("order-1");
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderService.adminList(1, 10, null, null, null, null, null, null, null))
                .thenReturn(page);

        // When
        Result<Map<String, Object>> result = adminOrderController.list(1, 10, null, null, null, null, null, null, null);

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().get("total"));
    }

    @Test
    void shouldListOrdersWithFilters() {
        // Given
        Page<Order> page = new PageImpl<>(Collections.emptyList());

        when(orderService.adminList(1, 10, "order-1", 100L, "13800138000", 1L, 1, "2024-01-01T00:00:00", "2024-12-31T23:59:59"))
                .thenReturn(page);

        // When
        Result<Map<String, Object>> result = adminOrderController.list(1, 10, "order-1", 100L, "13800138000", 1L, 1, "2024-01-01T00:00:00", "2024-12-31T23:59:59");

        // Then
        assertEquals(Result.SUCCESS, result.getCode());
        assertEquals(0L, result.getData().get("total"));
    }

    @Test
    void shouldReturnCorrectPaginationInfo() {
        // Given
        List<Order> orders = List.of(new Order(), new Order());
        Page<Order> page = new PageImpl<>(orders, org.springframework.data.domain.PageRequest.of(0, 10), 20);

        when(orderService.adminList(1, 10, null, null, null, null, null, null, null))
                .thenReturn(page);

        // When
        Result<Map<String, Object>> result = adminOrderController.list(1, 10, null, null, null, null, null, null, null);

        // Then
        Map<String, Object> data = result.getData();
        assertEquals(20L, data.get("total"));
        assertEquals(10, data.get("size"));
        assertEquals(1, data.get("current"));
        assertEquals(2, data.get("pages"));
    }

    @Test
    void shouldGetOrderDetailById() {
        // Given
        String orderId = "order-123";
        Order order = new Order();
        order.setId(orderId);
        when(orderService.findById(orderId)).thenReturn(order);

        // When
        Order result = adminOrderController.detail(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderService).findById(orderId);
    }

    @Test
    void shouldReturnNullWhenOrderNotFound() {
        // Given
        when(orderService.findById("non-existent")).thenReturn(null);

        // When
        Order result = adminOrderController.detail("non-existent");

        // Then
        assertNull(result);
    }

    @Test
    void shouldDeleteOrderById() {
        // Given
        String orderId = "order-123";
        when(orderService.deleteById(orderId)).thenReturn(true);

        // When
        Boolean result = adminOrderController.deleteById(orderId);

        // Then
        assertTrue(result);
        verify(orderService).deleteById(orderId);
    }

    @Test
    void shouldReturnFalseWhenDeleteFails() {
        // Given
        when(orderService.deleteById("non-existent")).thenReturn(false);

        // When
        Boolean result = adminOrderController.deleteById("non-existent");

        // Then
        assertFalse(result);
    }

    @Test
    void shouldBatchDeleteOrders() {
        // Given
        List<String> ids = List.of("order-1", "order-2", "order-3");
        when(orderService.batchDelete(ids)).thenReturn(true);

        // When
        Boolean result = adminOrderController.batchDelete(ids);

        // Then
        assertTrue(result);
        verify(orderService).batchDelete(ids);
    }

    @Test
    void shouldReturnFalseWhenBatchDeleteFails() {
        // Given
        List<String> ids = List.of("order-1", "order-2");
        when(orderService.batchDelete(ids)).thenReturn(false);

        // When
        Boolean result = adminOrderController.batchDelete(ids);

        // Then
        assertFalse(result);
    }
}
