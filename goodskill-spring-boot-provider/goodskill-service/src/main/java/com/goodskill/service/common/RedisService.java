package com.goodskill.service.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.goodskill.entity.Seckill;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private RedisTemplate<byte[], Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        String key = "seckill:" + seckillId;
        byte[] bytes = (byte[]) redisTemplate.opsForValue().get(key.getBytes());
        if (bytes != null) {
            Seckill seckill = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
            return seckill;
        } else {
            Seckill seckill = seckillMapper.selectById(seckillId);
            if (seckill == null) {
                throw new RuntimeException("秒杀活动不存在！");
            }
            putSeckill(seckill);
            return seckill;
        }
    }

    public void putSeckill(Seckill seckill) {
        String key = "seckill:" + seckill.getSeckillId();
        byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        int timeout = 60;
        redisTemplate.opsForValue().set(key.getBytes(), bytes, timeout, TimeUnit.SECONDS);
    }

    public void removeSeckill(long seckillId) {
        String key = "seckill:" + seckillId;
        redisTemplate.delete(key.getBytes());
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
