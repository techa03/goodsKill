package org.seckill.service.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

}
