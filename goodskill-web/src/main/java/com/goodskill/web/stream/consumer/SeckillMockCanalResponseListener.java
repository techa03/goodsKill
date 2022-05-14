package com.goodskill.web.stream.consumer;

import com.goodskill.api.dto.SeckillMockCanalResponseDTO;
import com.goodskill.api.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference
    private SeckillService seckillService;

    @Bean
    public Consumer<SeckillMockCanalResponseDTO> seckillCanalResult() {
        return responseDto -> {
            long seckillId = responseDto.getSeckillId();
            String note = responseDto.getNote();

            if (Boolean.TRUE.equals(responseDto.getStatus())) {
                log.info("binlog监控到秒杀活动结束，{}时间：{},秒杀id：{}", note, new Date(), seckillId);
                long successKillCount = seckillService.getSuccessKillCount(seckillId);
                log.info("最终成功交易笔数：{}", successKillCount);
            }
        };
    }
}
