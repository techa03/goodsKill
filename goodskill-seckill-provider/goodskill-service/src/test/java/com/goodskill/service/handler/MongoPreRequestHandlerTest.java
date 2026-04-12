package com.goodskill.service.handler;

import com.goodskill.core.feign.OrderFeignClient;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoPreRequestHandlerTest {
    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private MongoPreRequestHandler handler;

    private SeckillWebMockRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new SeckillWebMockRequestDTO();
        request.setSeckillId(1000L);
        request.setSeckillCount(100);
        request.setTaskId("task1");
    }

    @Test
    void testHandle() {
        handler.handle(request);

        verify(orderFeignClient, times(1)).deleteRecord(1000L);
    }

    @Test
    void testHandleWithDifferentSeckillId() {
        request.setSeckillId(2000L);

        handler.handle(request);

        verify(orderFeignClient, times(1)).deleteRecord(2000L);
    }

    @Test
    void testGetOrder() {
        int order = handler.getOrder();

        assertEquals(3, order);
    }
}
