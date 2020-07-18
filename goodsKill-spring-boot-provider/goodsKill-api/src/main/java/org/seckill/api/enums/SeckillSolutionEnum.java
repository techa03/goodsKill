package org.seckill.api.enums;

/**
 * 秒杀场景枚举
 *
 * @author techa03
 * @date 2019/4/4
 */
public enum SeckillSolutionEnum {
    /**
     *
     */
    SYCHRONIZED(1,"秒杀场景一(sychronized同步锁实现)"),
    REDISSION_LOCK(2,"秒杀场景二(redis分布式锁实现)"),
    ACTIVE_MQ(3,"秒杀场景三(activemq消息队列实现)"),
    KAFKA_MQ(4,"秒杀场景四(kafka消息队列实现)"),
    SQL_PROCEDURE(5,"秒杀场景五(存储过程实现)"),
    ACTIVE_MQ_MESSAGE_WITH_REPLY(6,"秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)"),
    ZOOKEEPER_LOCK(7, "秒杀场景七(zookeeper分布式锁)"),
    REDIS_MONGO_REACTIVE(8, "秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)"),
    RABBIT_MQ(9, "秒杀场景九(基于springcloud stream rabbitmq)");

    private int code;
    private String name;

    SeckillSolutionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
