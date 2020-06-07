package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * zk分布式锁工具类
 *
 * @author heng
 */
@Slf4j
@Component
public class ZookeeperLockUtil {

    private CuratorFramework client;
    @Value("${zookeeper_ip}")
    private String zookeeperIp;
    private ThreadLocal<Map<Long, InterProcessMutex>> threadLock = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * 采用curator提供的互斥锁获取锁方法
     * 采用线程本地变量的方法解决并发问题
     *
     * @param seckillId 秒杀id
     * @return true 加锁成功
     */
    public boolean lock(long seckillId) {
        try {
            Map<Long, InterProcessMutex> map;
            String rootLockPath = "/goodskill";
            Map<Long, InterProcessMutex> processMutexMap = threadLock.get();
            if (processMutexMap.get(seckillId) == null) {
                processMutexMap.put(seckillId, new InterProcessMutex(client, rootLockPath + "/" + seckillId));
            }
            boolean acquire = processMutexMap.get(seckillId).acquire(5000L, TimeUnit.MILLISECONDS);
            if (log.isDebugEnabled()) {
                log.debug("成功获取到zk锁,秒杀id{}", seckillId);
            }
            return acquire;
        } catch (Exception e) {
            log.warn("获取zk锁异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 采用curator提供的互斥锁释放方法
     *
     * @param seckillId 秒杀id
     * @return true 释放锁成功
     */
    public boolean releaseLock(long seckillId) {
        try {
            Map<Long, InterProcessMutex> processMutexMap = threadLock.get();
            processMutexMap.get(seckillId).release();
            // 释放内存资源
            processMutexMap.remove(seckillId);
            if (log.isDebugEnabled()) {
                log.debug("zk锁已释放，秒杀id{}", seckillId);
            }
            return true;
        } catch (Exception e) {
            log.warn("释放zk锁异常:{}", e.getMessage());
            return false;
        }
    }

    @PostConstruct
    private void initClient() {
        // 初始化Curator客户端
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(zookeeperIp, retryPolicy);
        client.start();
    }

    @PreDestroy
    private void stopLocal() {
        threadLock.remove();
        client.close();
        log.info("zk锁资源已释放！");
    }

    private ZookeeperLockUtil() {
    }

}
