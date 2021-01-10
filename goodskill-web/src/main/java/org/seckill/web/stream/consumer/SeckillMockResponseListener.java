package org.seckill.web.stream.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.seckill.api.dto.SeckillMockResponseDto;
import org.seckill.api.enums.SeckillSolutionEnum;
import org.seckill.api.service.SeckillService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import java.util.Date;

/**
 * 秒杀
 */
@EnableBinding(value = {Sink.class})
@Slf4j
public class SeckillMockResponseListener {
    @Reference
    private SeckillService seckillService;

    @StreamListener(Sink.INPUT)
    public void consume(SeckillMockResponseDto responseDto) throws InterruptedException {
        long seckillId = responseDto.getSeckillId();
        String note = responseDto.getNote();

        if (Boolean.TRUE == responseDto.getStatus()) {
            log.info("秒杀活动结束，{}时间：{},秒杀id：{}", note, new Date(), seckillId);
            long successKillCount = seckillService.getSuccessKillCount(seckillId);
            long temp = 0;
            while (successKillCount != temp) {
                // 计算总数可能有延迟，等待统计数据稳定后得到最终结果
                log.info("最终成功交易笔数统计中。。。");
                successKillCount = temp;
                Thread.sleep(1500L);
                temp = seckillService.getSuccessKillCount(seckillId);
            }
            log.info("最终成功交易笔数：{}", successKillCount);
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
    }
}
