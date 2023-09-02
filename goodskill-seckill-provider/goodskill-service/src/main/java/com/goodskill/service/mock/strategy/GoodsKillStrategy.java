package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;

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
    void execute(SeckillMockRequestDTO requestDto);
}
