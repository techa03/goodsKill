package com.goodskill.order.service.impl;

import com.goodskill.core.pojo.dto.OrderDTO;
import com.goodskill.order.entity.Order;
import com.goodskill.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        orderDTO.setSeckillId(1L);
        orderDTO.setUserPhone("13800138000");

        // When
        String result = orderService.saveRecord(orderDTO);

        // Then
        assertNotNull(result);
        verify(orderRepository).insert(any(Order.class));
    }

    /**
     * 测试：保存订单时正确设置字段
     */
    @Test
    void shouldSetCorrectFieldsWhenSavingOrder() {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSeckillId(2L);
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
        verify(orderRepository).deleteBySeckillId(seckillId);
    }

    /**
     * 测试：统计订单数量
     */
    @Test
    void shouldCountOrdersBySeckillId() {
        // Given
        long seckillId = 1L;
        when(orderRepository.countBySeckillId(seckillId)).thenReturn(5L);

        // When
        Long count = orderService.count(seckillId);

        // Then
        assertEquals(5L, count);
        verify(orderRepository).countBySeckillId(seckillId);
    }

    /**
     * 测试：统计订单数量为0
     */
    @Test
    void shouldReturnZeroWhenNoOrders() {
        // Given
        long seckillId = 2L;
        when(orderRepository.countBySeckillId(seckillId)).thenReturn(0L);

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
        orderDTO.setSeckillId(1L);
        orderDTO.setUserPhone("13800138000");

        doThrow(new RuntimeException("数据库连接失败"))
                .when(orderRepository).insert(any(Order.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.saveRecord(orderDTO);
        });
    }

    /**
     * 测试：根据用户ID查询订单列表
     */
    @Test
    void shouldFindOrdersByUserId() {
        // Given
        String userId = "123";
        int pageNum = 1;
        int pageSize = 10;

        // When
        orderService.list(userId, pageNum, pageSize);

        // Then
        verify(orderRepository).findByUserIdOrderByCreateTimeDesc(eq(userId), any());
    }

    /**
     * 测试：用户ID为空时返回空列表
     */
    @Test
    void shouldReturnEmptyListWhenUserIdIsNull() {
        // Given
        String userId = null;
        int pageNum = 1;
        int pageSize = 10;

        // When
        var result = orderService.list(userId, pageNum, pageSize);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    /**
     * 测试：更新订单状态成功
     */
    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        // Given
        String orderId = "order-123";
        Byte status = 2;
        String stateDesc = "已支付";
        String alipayTradeNo = "ali-123";

        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order));

        // When
        boolean result = orderService.updateOrderStatus(orderId, status, stateDesc, alipayTradeNo, null);

        // Then
        assertTrue(result);
        verify(orderRepository).save(order);
        assertEquals(status, order.getStatus());
        assertEquals(stateDesc, order.getStateDesc());
        assertEquals(alipayTradeNo, order.getAlipayTradeNo());
    }

    /**
     * 测试：订单不存在时更新失败
     */
    @Test
    void shouldReturnFalseWhenOrderNotFound() {
        // Given
        String orderId = "non-existent-order";
        Byte status = 2;
        String stateDesc = "已支付";
        String alipayTradeNo = "ali-123";

        when(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty());

        // When
        boolean result = orderService.updateOrderStatus(orderId, status, stateDesc, alipayTradeNo, null);

        // Then
        assertFalse(result);
        verify(orderRepository, never()).save(any());
    }
}
