package org.seckill.service.inner;

public interface SeckillExecutor {

    /**
     * 秒杀内部处理方法
     * @param seckillId
     * @param userPhone
     * @param note
     */
    void dealSeckill(long seckillId, String userPhone, String note);
}
