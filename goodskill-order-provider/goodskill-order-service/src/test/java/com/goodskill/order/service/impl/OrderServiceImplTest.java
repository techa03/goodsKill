package com.goodskill.order.service.impl;

import com.goodskill.order.entity.Order;
import com.goodskill.order.entity.OrderDTO;
import com.goodskill.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OrderServiceImpl 单元测试
 * 测试订单服务的核心功能
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    /**
     * 测试：保存订单成功
     */
    @Test
    void shouldSaveOrderSuccessfully() {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(BigInteger.valueOf(1L));
        orderDTO.setUserPhone("13800138000");

        // When
        Boolean result = orderService.saveRecord(orderDTO);

        // Then
        assertTrue(result);
        verify(orderRepository).insert(any(Order.class));
    }

    /**
     * 测试：保存订单时正确设置字段
     */
    @Test
    void shouldSetCorrectFieldsWhenSavingOrder() {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(BigInteger.valueOf(2L));
        orderDTO.setUserPhone("13900139000");
        orderDTO.setStatus((byte) 1);

        // When
        orderService.saveRecord(orderDTO);

        // Then
        verify(orderRepository).insert(any(Order.class));
    }

    /**
     * 测试：根据秒杀ID删除记录
     */
    @Test
    void shouldDeleteRecordBySeckillId() {
        // Given
        long seckillId = 1L;

        // When
        Boolean result = orderService.deleteRecord(seckillId);

        // Then
        assertTrue(result);
        verify(orderRepository).deleteBySeckillId(BigInteger.valueOf(seckillId));
    }

    /**
     * 测试：统计订单数量
     */
    @Test
    void shouldCountOrdersBySeckillId() {
        // Given
        long seckillId = 1L;
        when(orderRepository.countBySeckillId(BigInteger.valueOf(seckillId))).thenReturn(5L);

        // When
        Long count = orderService.count(seckillId);

        // Then
        assertEquals(5L, count);
        verify(orderRepository).countBySeckillId(BigInteger.valueOf(seckillId));
    }

    /**
     * 测试：统计订单数量为0
     */
    @Test
    void shouldReturnZeroWhenNoOrders() {
        // Given
        long seckillId = 2L;
        when(orderRepository.countBySeckillId(BigInteger.valueOf(seckillId))).thenReturn(0L);

        // When
        Long count = orderService.count(seckillId);

        // Then
        assertEquals(0L, count);
    }

    /**
     * 测试：仓库异常处理
     */
    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(BigInteger.valueOf(1L));
        orderDTO.setUserPhone("13800138000");

        doThrow(new RuntimeException("数据库连接失败"))
                .when(orderRepository).insert(any(Order.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.saveRecord(orderDTO);
        });
    }
}
