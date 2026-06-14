package com.goodskill.order.controller;

import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.OrderDTO;
import com.goodskill.core.util.UserInfoUtil;
import com.goodskill.order.entity.Order;
import com.goodskill.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OrderController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderServiceImpl orderService;

    @Test
    void shouldDeleteRecordSuccessfully() {
        // Given
        long seckillId = 1L;
        when(orderService.deleteRecord(seckillId)).thenReturn(true);

        // When
        Boolean result = orderController.deleteRecord(seckillId);

        // Then
        assertTrue(result);
        verify(orderService).deleteRecord(seckillId);
    }

    @Test
    void shouldSaveRecordSuccessfully() {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(1L);
        orderDTO.setUserPhone("13800138000");
        when(orderService.saveRecord(any(OrderDTO.class))).thenReturn("order-123");

        // When
        String result = orderController.saveRecord(orderDTO);

        // Then
        assertEquals("order-123", result);
        verify(orderService).saveRecord(orderDTO);
    }

    @Test
    void shouldCountOrdersBySeckillId() {
        // Given
        long seckillId = 1L;
        when(orderService.count(seckillId)).thenReturn(10L);

        // When
        Long result = orderController.count(seckillId);

        // Then
        assertEquals(10L, result);
        verify(orderService).count(seckillId);
    }

    @Test
    void shouldReturnZeroCountWhenNoOrders() {
        // Given
        long seckillId = 999L;
        when(orderService.count(seckillId)).thenReturn(0L);

        // When
        Long result = orderController.count(seckillId);

        // Then
        assertEquals(0L, result);
    }

    @Test
    void shouldListOrdersForUser() {
        // Given
        Order order = new Order();
        order.setId("order-1");
        Page<Order> page = new PageImpl<>(List.of(order), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        try (MockedStatic<UserInfoUtil> mockedUtil = mockStatic(UserInfoUtil.class)) {
            mockedUtil.when(UserInfoUtil::getUserId).thenReturn("user-123");
            when(orderService.list("user-123", 1, 10)).thenReturn(page);

            // When
            Result<Map<String, Object>> result = orderController.list(1, 10);

            // Then
            assertEquals(Result.SUCCESS, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1L, result.getData().get("total"));
            assertEquals(10, result.getData().get("size"));
        }
    }

    @Test
    void shouldListOrdersWithEmptyResult() {
        // Given
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());

        try (MockedStatic<UserInfoUtil> mockedUtil = mockStatic(UserInfoUtil.class)) {
            mockedUtil.when(UserInfoUtil::getUserId).thenReturn("user-456");
            when(orderService.list("user-456", 1, 10)).thenReturn(emptyPage);

            // When
            Result<Map<String, Object>> result = orderController.list(1, 10);

            // Then
            assertEquals(Result.SUCCESS, result.getCode());
            assertEquals(0L, result.getData().get("total"));
        }
    }

    @Test
    void shouldGetOrderDetailById() {
        // Given
        String orderId = "order-123";
        Order order = new Order();
        order.setId(orderId);
        order.setUserPhone("13800138000");
        when(orderService.findById(orderId)).thenReturn(order);

        // When
        Order result = orderController.detail(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderService).findById(orderId);
    }

    @Test
    void shouldReturnNullWhenOrderNotFound() {
        // Given
        String orderId = "non-existent";
        when(orderService.findById(orderId)).thenReturn(null);

        // When
        Order result = orderController.detail(orderId);

        // Then
        assertNull(result);
        verify(orderService).findById(orderId);
    }
}
