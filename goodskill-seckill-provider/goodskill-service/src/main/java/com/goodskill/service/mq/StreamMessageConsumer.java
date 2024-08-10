package com.goodskill.service.mq;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.core.enums.SeckillSolutionEnum;
import com.goodskill.service.inner.SeckillExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class StreamMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(StreamMessageConsumer.class);

    private SeckillExecutor seckillExecutor;

    @Autowired
    public void setSeckillExecutor(SeckillExecutor seckillExecutor) {
        this.seckillExecutor = seckillExecutor;
    }

    @Bean
    public Consumer<SeckillMockRequestDTO> seckill() {
        return  (payload) -> {
            log.info("收到秒杀请求:{}", payload);
            String userPhone = payload.getPhoneNumber();
            Long seckillId = payload.getSeckillId();
            seckillExecutor.dealSeckill(seckillId, userPhone, SeckillSolutionEnum.RABBIT_MQ.name(), payload.getTaskId());
        };
    }
}
