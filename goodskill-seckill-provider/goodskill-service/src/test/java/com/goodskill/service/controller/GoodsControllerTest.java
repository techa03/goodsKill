package com.goodskill.service.controller;

import com.goodskill.core.feign.OrderFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsControllerTest {
    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private GoodsController goodsController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCountOrders() {
        Long seckillId = 1000L;
        Long expectedCount = 10L;

        when(orderFeignClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderFeignClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithZero() {
        Long seckillId = 1001L;
        Long expectedCount = 0L;

        when(orderFeignClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderFeignClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithLargeNumber() {
        Long seckillId = 1002L;
        Long expectedCount = 999999L;

        when(orderFeignClient.count(seckillId)).thenReturn(expectedCount);

        Long result = goodsController.countOrders(seckillId);

        assertEquals(expectedCount, result);
        verify(orderFeignClient, times(1)).count(seckillId);
    }

    @Test
    void testCountOrdersWithException() {
        Long seckillId = 1003L;

        when(orderFeignClient.count(seckillId)).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> {
            goodsController.countOrders(seckillId);
        });

        verify(orderFeignClient, times(1)).count(seckillId);
    }
}
