package com.goodskill.service.mock.strategy;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.service.inner.SeckillExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.goodskill.common.enums.SeckillSolutionEnum.ATOMIC_UPDATE;

/**
 * @author techa03
 * @date 2021/2/6
 */
@Component
@Slf4j
public class SentinelLimitStrategy implements GoodsKillStrategy {
    @Autowired
    private SeckillExecutor seckillExecutor;

    @Override
    @SentinelResource(value = "limit", blockHandler = "exceptionHandler")
    public void execute(SeckillMockRequestDTO requestDto) {
        seckillExecutor.dealSeckill(requestDto.getSeckillId(), requestDto.getPhoneNumber(), ATOMIC_UPDATE.getName(), requestDto.getTaskId());
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public void exceptionHandler(long seckillId, String userPhone, String note, BlockException ex) {
        log.error("Oops, error occurred at {}:{}:{}", seckillId, userPhone, note, ex);
    }
}
