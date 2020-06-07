package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.service.inner.SeckillExecutor;
import org.seckill.service.util.ZookeeperLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.seckill.api.enums.SeckillSolutionEnum.ZOOKEEPER_LOCK;

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
    public void execute(SeckillMockRequestDto requestDto) {
        long seckillId = requestDto.getSeckillId();
        if (zookeeperLockUtil.lock(seckillId)) {
            try {
                seckillExecutor.dealSeckill(seckillId, requestDto.getPhoneNumber(), ZOOKEEPER_LOCK.getName());
            } finally {
                zookeeperLockUtil.releaseLock(seckillId);
            }
        }
    }
}
