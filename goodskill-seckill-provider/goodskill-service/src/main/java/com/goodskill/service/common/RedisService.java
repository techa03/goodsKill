package com.goodskill.service.common;

import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mapper.SeckillMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author heng
 * @date 2019/2/25
 */
@Component
@Slf4j
public class RedisService {
    @Autowired
    private SeckillMapper seckillMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    public Seckill getSeckill(long seckillId) {
        String key = "seckill:" + seckillId;
        Seckill seckill = (Seckill) redisTemplate.opsForValue().get(key);
        if (seckill != null) {
            return seckill;
        } else {
            seckill = seckillMapper.selectById(seckillId);
            if (seckill == null) {
                throw new RuntimeException("秒杀活动不存在！");
            }
            putSeckill(seckill);
            return seckill;
        }
    }

    public void putSeckill(Seckill seckill) {
        String key = "seckill:" + seckill.getSeckillId();
        int timeout = 60;
        redisTemplate.opsForValue().set(key, seckill, timeout, TimeUnit.SECONDS);
    }

    public void removeSeckill(long seckillId) {
        String key = "seckill:" + seckillId;
        redisTemplate.delete(key);
        redisTemplate.delete(String.valueOf(seckillId));
    }

    /**
     * 设置秒杀活动已结束
     *
     * @param seckillId
     */
    public Boolean setSeckillEndFlag(long seckillId, String taskId) {
        return stringRedisTemplate.opsForValue().setIfAbsent("goodskill:seckill:end:notice" + seckillId + ":" + taskId, "1");
    }

    /**
     * 清除秒杀活动结束标识
     *
     * @param seckillId
     */
    public Boolean clearSeckillEndFlag(long seckillId, String taskId) {
        return stringRedisTemplate.delete("goodskill:seckill:end:notice" + seckillId + ":" + taskId);
    }

}
