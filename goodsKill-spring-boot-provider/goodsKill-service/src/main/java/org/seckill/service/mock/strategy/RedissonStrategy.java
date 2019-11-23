package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.seckill.api.enums.SeckillSolutionEnum.REDISSION_LOCK;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class RedissonStrategy implements GoodsKillStrategy, InitializingBean {
    private RedissonClient redissonClient;
    @Value("${cache_ip_address}")
    private String cacheIpAddress;
    @Autowired
    private SeckillExecutor seckillExecutor;


    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        RLock lock = redissonClient.getLock(requestDto.getSeckillId() + "");
        lock.lock();
        try {
            seckillExecutor.dealSeckill(requestDto.getSeckillId(), requestDto.getPhoneNumber(), REDISSION_LOCK.getName());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress(cacheIpAddress);
        redissonClient = Redisson.create(config);
    }
}
