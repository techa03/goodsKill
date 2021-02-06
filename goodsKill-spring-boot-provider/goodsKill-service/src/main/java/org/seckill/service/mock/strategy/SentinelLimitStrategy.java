package org.seckill.service.mock.strategy;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.seckill.api.enums.SeckillSolutionEnum.ATOMIC_UPDATE;

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
    public void execute(SeckillMockRequestDto requestDto) {
        seckillExecutor.dealSeckill(requestDto.getSeckillId(), requestDto.getPhoneNumber(), ATOMIC_UPDATE.getName());
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public void exceptionHandler(long seckillId, String userPhone, String note, BlockException ex) {
        log.error("Oops, error occurred at {}:{}:{}", seckillId, userPhone, note, ex);
    }
}
