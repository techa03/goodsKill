package com.goodskill.service.common.enums;

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
    SYNCHRONIZED(1, "Synchronized同步锁", SynchronizedLockStrategy.class.getName()),
    REDISSON(2, "redis分布式锁", RedissonStrategy.class.getName()),
    ATOMIC_UPDATE(5, "数据库原子性更新", AtomicUpdateStrategy.class.getName()),
    ZOOKEEPER_LOCK(7, "zookeeper分布式锁", ZookeeperLockStrategy.class.getName()),
    REDIS_MONGO_REACTIVE(8, "秒杀商品存放redis减库存，异步发送秒杀成功MQ", RedisMongoReactiveStrategy.class.getName()),
    SENTINEL_LIMIT(10, "Sentinel限流+数据库原子性更新", SentinelLimitStrategy.class.getName()),
    ATOMIC_CANAL(11, "数据库原子性更新+canal 数据库binlog日志监听秒杀结果", AtomicWithCanalStrategy.class.getName()),

    ;


    private final int code;

    private final String strategyName;

    private final String className;

    public static GoodsKillStrategyEnum stateOf(int code) {
        for (GoodsKillStrategyEnum state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        return null;
    }
}
