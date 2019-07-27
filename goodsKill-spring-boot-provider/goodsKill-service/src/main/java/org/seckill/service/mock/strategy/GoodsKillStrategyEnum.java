package org.seckill.service.mock.strategy;

/**
 * @author heng
 */
public enum GoodsKillStrategyEnum {

    /**
     *
     */
    SYNCHRONIZED(1, "Synchronized同步锁", new SynchronizedLockStrategy()),

    REDISSON(2, "redis分布式锁", new RedissonStrategy()),

    PROCEDURE(5, "数据库存储过程", new ProcedureStrategy()),

    ZOOKEEPER_LOCK(7, "zookeeper分布式锁", new ZookeeperLockStrategy()),

    REDIS_MONGO_REACTIVE(8, "秒杀商品存放redis减库存，异步发送秒杀成功MQ", new RedisMongoReactiveStrategy()),
    ;


    private int code;

    private String strategyName;

    private GoodsKillStrategy goodsKillStrategy;

    public String getStrategyName() {
        return strategyName;
    }

    public GoodsKillStrategy getGoodsKillStrategy() {
        return goodsKillStrategy;
    }

    public int getCode() {
        return code;
    }

    GoodsKillStrategyEnum(int code, String strategyName, GoodsKillStrategy goodsKillStrategy) {
        this.code = code;
        this.strategyName = strategyName;
        this.goodsKillStrategy = goodsKillStrategy;
    }

    public static GoodsKillStrategyEnum stateOf(int code) {
        for (GoodsKillStrategyEnum state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        return null;
    }
}
