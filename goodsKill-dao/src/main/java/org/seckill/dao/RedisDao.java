package org.seckill.dao;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by heng on 2017/2/25.
 */
@Repository
public class RedisDao {
    @Autowired
    private SeckillMapper seckillMapper;
    public static final Logger logger = LoggerFactory.getLogger(RedisDao.class);
    private JedisPool jedisPool;
    private Jedis jedis;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        Jedis jedis = getJedisConnection();
        String key = "seckill:" + seckillId;
        byte[] bytes = jedis.get(key.getBytes());
        if (bytes != null) {
            Seckill seckill = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
            return seckill;
        } else {
            Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
            if (seckill == null) {
                throw new RuntimeException("秒杀活动不存在！");
            }
            putSeckill(seckill);
            return seckill;
        }
    }

    private String putSeckill(Seckill seckill) {
        Jedis jedis = jedisPool.getResource();
        String key = "seckill:" + seckill.getSeckillId();
        byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        int timeout = 60;
        return jedis.setex(key.getBytes(), timeout, bytes);
    }

    public RedisDao() {
    }

    public String put(String key, Object value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.lpush(key, value.toString());
        } finally {
            jedis.close();
        }
        return null;
    }

    public String get() {
        return null;
    }

    private Jedis getJedisConnection(){
        if(jedis != null){
            return jedis;
        }else{
            synchronized (RedisDao.class){
                jedis = jedisPool.getResource();
                return jedis;
            }
        }
    }
}
