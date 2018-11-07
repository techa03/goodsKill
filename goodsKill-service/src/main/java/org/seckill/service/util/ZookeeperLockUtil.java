package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ZookeeperLockUtil implements InitializingBean {

    private CuratorFramework client;
    private String ROOT_LOCK_PATH = "/goodsKill";
    private ConcurrentHashMap<Long, InterProcessMutex> lockMap = new ConcurrentHashMap();


    public static void main(String[] args) {
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        client = CuratorFrameworkFactory.newClient(PropertiesUtil.getProperty("zookeeper_ip"), retryPolicy);
//        client.start();
//
//        long seckillId = 1003L;
//        lock = new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId));
//        new ZookeeperLockUtil().lock(seckillId);
//        Thread.sleep(10000);
//        new ZookeeperLockUtil().releaseLock(seckillId);
    }

    public boolean lock(long seckillId) {
        try {
            if (lockMap.get(seckillId) == null) {
                lockMap.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH+"/"+String.valueOf(seckillId)));
            }
            lockMap.get(seckillId).acquire(2L, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean releaseLock(long seckillId) {
        try {
            lockMap.get(seckillId).release();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.newClient(PropertiesUtil.getProperty("zookeeper_ip"), retryPolicy);
        client.start();
    }
}
