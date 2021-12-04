package org.seckill.web.stream.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.seckill.api.dto.SeckillMockResponseDto;
import org.seckill.api.enums.SeckillSolutionEnum;
import org.seckill.api.service.SeckillService;
import org.seckill.web.util.TaskTimeCaculateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.function.Consumer;

/**
 * 秒杀
 */
@Slf4j
@Configuration
public class SeckillMockResponseListener {
    @DubboReference
    private SeckillService seckillService;

    @Bean
    public Consumer<SeckillMockResponseDto> seckillResult() {
        return responseDto -> {
            long seckillId = responseDto.getSeckillId();
            String note = responseDto.getNote();

            if (Boolean.TRUE.equals(responseDto.getStatus())) {
                log.info("秒杀活动结束，{}时间：{},秒杀id：{}", note, new Date(), seckillId);
                long successKillCount = seckillService.getSuccessKillCount(seckillId);
                long temp = 0;
                while (successKillCount != temp) {
                    // 计算总数可能有延迟，等待统计数据稳定后得到最终结果
                    log.info("最终成功交易笔数统计中。。。");
                    successKillCount = temp;
                    try {
                        Thread.sleep(1500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    temp = seckillService.getSuccessKillCount(seckillId);
                }
                TaskTimeCaculateUtil.stop();
                log.info("最终成功交易笔数：{}", successKillCount);
                log.info("历史任务耗时统计：{}", TaskTimeCaculateUtil.prettyPrint());
                // 场景八由于是异步插入，补偿查询最新插入记录数，延迟10s
                try {
                    if (SeckillSolutionEnum.REDIS_MONGO_REACTIVE.getName().endsWith(note)) {
                        Thread.sleep(10000);
                        log.info("mongo最终成功交易笔数：{}", successKillCount);
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}
