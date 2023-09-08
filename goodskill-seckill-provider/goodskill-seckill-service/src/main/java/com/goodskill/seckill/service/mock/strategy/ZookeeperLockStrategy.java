package com.goodskill.seckill.service.mock.strategy;

import com.goodskill.seckill.api.dto.SeckillMockRequestDTO;
import com.goodskill.seckill.common.util.ZookeeperLockUtil;
import com.goodskill.seckill.service.SeckillExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.goodskill.common.core.enums.SeckillSolutionEnum.ZOOKEEPER_LOCK;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class ZookeeperLockStrategy implements GoodsKillStrategy {
    @Autowired
    private ZookeeperLockUtil zookeeperLockUtil;
    @Autowired
    private SeckillExecutor seckillExecutor;

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        long seckillId = requestDto.getSeckillId();
        if (zookeeperLockUtil.lock(seckillId)) {
            try {
                seckillExecutor.dealSeckill(seckillId, requestDto.getPhoneNumber(), ZOOKEEPER_LOCK.getName(), requestDto.getTaskId());
            } finally {
                zookeeperLockUtil.releaseLock(seckillId);
            }
        }
    }
}
