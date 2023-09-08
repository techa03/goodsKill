package com.goodskill.seckill.service;

/**
 * @author heng
 */
public interface SeckillExecutor {

    /**
     * 秒杀内部处理方法
     *
     * @param seckillId
     * @param userPhone
     * @param note
     * @param taskId
     */
    void dealSeckill(long seckillId, String userPhone, String note, String taskId);
}
