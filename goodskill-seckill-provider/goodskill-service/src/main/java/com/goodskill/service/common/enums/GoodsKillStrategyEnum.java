package com.goodskill.service.common.enums;

import com.goodskill.common.core.enums.SeckillSolutionEnum;
import com.goodskill.service.mock.strategy.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀策略枚举，通过枚举避免暴露具体方法
 *
 * @author heng
 */
@Getter
@AllArgsConstructor
public enum GoodsKillStrategyEnum {

    /**
     *
     */
    SYNCHRONIZED(SeckillSolutionEnum.SYCHRONIZED, "Synchronized同步锁", SynchronizedLockStrategy.class.getName()),
    REDISSON(SeckillSolutionEnum.REDISSION_LOCK, "redis分布式锁", RedissonStrategy.class.getName()),
    ATOMIC_UPDATE(SeckillSolutionEnum.ATOMIC_UPDATE, "数据库原子性更新", AtomicUpdateStrategy.class.getName()),
    ZOOKEEPER_LOCK(SeckillSolutionEnum.ZOOKEEPER_LOCK, "zookeeper分布式锁", ZookeeperLockStrategy.class.getName()),
    REDIS_MONGO_REACTIVE(SeckillSolutionEnum.REDIS_MONGO_REACTIVE, "秒杀商品存放redis减库存，异步发送秒杀成功MQ", RedisMongoReactiveStrategy.class.getName()),
    SENTINEL_LIMIT(SeckillSolutionEnum.SENTINEL_LIMIT, "Sentinel限流+数据库原子性更新", SentinelLimitStrategy.class.getName()),
    ATOMIC_CANAL(SeckillSolutionEnum.ATOMIC_CANAL, "数据库原子性更新+canal 数据库binlog日志监听秒杀结果", AtomicWithCanalStrategy.class.getName()),
    ;

    private final SeckillSolutionEnum seckillSolutionEnum;

    private final String strategyName;

    private final String className;

    public static GoodsKillStrategyEnum stateOf(int code) {
        for (GoodsKillStrategyEnum state : values()) {
            if (state.seckillSolutionEnum.getCode() == code) {
                return state;
            }
        }
        return null;
    }
}
