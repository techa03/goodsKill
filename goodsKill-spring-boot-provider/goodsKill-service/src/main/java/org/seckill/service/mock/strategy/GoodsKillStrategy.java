package org.seckill.service.mock.strategy;

import org.seckill.api.dto.SeckillMockRequestDto;

/**
 * @author heng
 */
public interface GoodsKillStrategy {

    /**
     * @param requestDto
     */
    void execute(SeckillMockRequestDto requestDto);
}
