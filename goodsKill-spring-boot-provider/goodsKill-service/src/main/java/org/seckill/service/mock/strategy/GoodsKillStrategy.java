package org.seckill.service.mock.strategy;

import org.seckill.api.dto.SeckillMockRequestDto;

/**
 * 秒杀策略接口
 *
 * @author heng
 */
public interface GoodsKillStrategy {

    /**
     * 执行秒杀
     *
     * @param requestDto 秒杀请求
     */
    void execute(SeckillMockRequestDto requestDto);
}
