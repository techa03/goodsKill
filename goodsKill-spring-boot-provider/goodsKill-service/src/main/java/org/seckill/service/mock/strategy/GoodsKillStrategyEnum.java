package org.seckill.service.mock.strategy;

/**
 * @author heng
 */
public enum GoodsKillStrategyEnum {

    /**
     *
     */
    SYNCHRONIZED(1, "Synchronized同步锁", SynchronizedLockStrategy.class.getName()),

    REDISSON(2, "redis分布式锁", RedissonStrategy.class.getName()),

    PROCEDURE(5, "数据库存储过程", ProcedureStrategy.class.getName()),

    ZOOKEEPER_LOCK(7, "zookeeper分布式锁", ZookeeperLockStrategy.class.getName()),

    REDIS_MONGO_REACTIVE(8, "秒杀商品存放redis减库存，异步发送秒杀成功MQ", RedisMongoReactiveStrategy.class.getName()),
    ;


    private int code;

    private String strategyName;

    private String className;

    public String getStrategyName() {
        return strategyName;
    }

    public String getClassName() {
        return className;
    }

    public int getCode() {
        return code;
    }

    GoodsKillStrategyEnum(int code, String strategyName, String className) {
        this.code = code;
        this.strategyName = strategyName;
        this.className = className;
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
