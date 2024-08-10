package com.goodskill.service.handler;

import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.order.api.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoPreRequestHandler extends AbstractPreRequestHandler {
    @Resource
    private OrderService orderService;

    @Override
    public void handle(SeckillWebMockRequestDTO request) {
        orderService.deleteRecord(request.getSeckillId());
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
