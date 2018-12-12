package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper分布式锁工具类
 * @author heng
 */
@Component
@Slf4j
public class ZookeeperLockUtil implements InitializingBean {

    private CuratorFramework client;
    private String ROOT_LOCK_PATH = "/goodsKill";
    private ThreadLocal<Map<Long, InterProcessMutex>> threadLock = new ThreadLocal<>();

    /**
     * 采用curator提供的互斥锁获取锁方法
     * 采用线程本地变量的方法解决并发问题
     * @param seckillId
     * @return
     */
    public boolean lock(long seckillId) {

        try {
            Map<Long, InterProcessMutex> map;
            if (threadLock.get() == null) {
                map = new ConcurrentHashMap();
                map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
                threadLock.set(map);
            } else {
                if (threadLock.get().get(seckillId) == null) {
                    map = threadLock.get();
                    map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
                }
            }
            threadLock.get().get(seckillId).acquire(5000L, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 采用curator提供的互斥锁释放方法
     * @param seckillId
     * @return
     */
    public boolean releaseLock(long seckillId) {
        try {
            threadLock.get().get(seckillId).release();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 初始化curator客户端
     */
    @Override
    public void afterPropertiesSet() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(PropertiesUtil.getProperty("zookeeper_ip"), retryPolicy);
        client.start();
    }

}
