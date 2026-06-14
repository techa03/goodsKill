package com.goodskill.web.stream.consumer;

import com.goodskill.api.dto.SeckillMockCanalResponseDTO;
import com.goodskill.core.rest.client.SeckillRestClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.function.Consumer;

/**
 * canal binlog秒杀结果消息消费者
 */
@Slf4j
@Configuration
public class SeckillMockCanalResponseListener {
    @Resource
    private SeckillRestClient seckillRestClient;

    @Bean
    public Consumer<SeckillMockCanalResponseDTO> seckillCanalResult() {
        return responseDto -> {
            long seckillId = responseDto.getSeckillId();
            String note = responseDto.getNote();

            if (Boolean.TRUE.equals(responseDto.getStatus())) {
                log.info("binlog监控到秒杀活动结束，{}时间：{},秒杀id：{}", note, new Date(), seckillId);
                long successKillCount = seckillRestClient.getSuccessKillCount(seckillId);
                log.info("最终成功交易笔数：{}", successKillCount);
            }
        };
    }
}
