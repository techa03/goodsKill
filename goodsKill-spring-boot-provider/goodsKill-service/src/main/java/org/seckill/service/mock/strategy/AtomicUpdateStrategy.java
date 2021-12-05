package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.SeckillMockRequestDTO;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.goodskill.common.enums.SeckillSolutionEnum.ATOMIC_UPDATE;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class AtomicUpdateStrategy implements GoodsKillStrategy {
    @Autowired
    private SeckillExecutor seckillExecutor;

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        seckillExecutor.dealSeckill(requestDto.getSeckillId(), requestDto.getPhoneNumber(), ATOMIC_UPDATE.getName());
    }
}
