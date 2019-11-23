package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.seckill.api.enums.SeckillSolutionEnum.SQL_PROCEDURE;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class ProcedureStrategy implements GoodsKillStrategy {
    @Autowired
    private SeckillExecutor seckillExecutor;

    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        seckillExecutor.dealSeckill(requestDto.getSeckillId(), requestDto.getPhoneNumber(), SQL_PROCEDURE.getName());
    }
}
