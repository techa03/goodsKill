package com.goodskill.service.handler;

import com.goodskill.core.feign.OrderRestClient;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoPreRequestHandler extends AbstractPreRequestHandler {
    @Resource
    private OrderRestClient orderRestClient;

    @Override
    public void handle(SeckillWebMockRequestDTO request) {
        orderRestClient.deleteRecord(request.getSeckillId());
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
