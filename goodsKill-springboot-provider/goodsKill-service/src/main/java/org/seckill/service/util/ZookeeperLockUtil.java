package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ZookeeperLockUtil implements ApplicationListener {

    private CuratorFramework client;
    private String ROOT_LOCK_PATH = "/goodsKill";
    private ThreadLocal<Map<Long, InterProcessMutex>> threadLock = new ThreadLocal<>();

    public boolean lock(long seckillId) {
        try {
            Map<Long, InterProcessMutex> map;
            if (threadLock.get() == null) {
                map = new HashMap();
                map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
                threadLock.set(map);
            } else {
                if (threadLock.get().get(seckillId) == null) {
                    map = threadLock.get();
                    map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
                }
                threadLock.get().get(seckillId).acquire(2L, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean releaseLock(long seckillId) {
        try {
            threadLock.get().get(seckillId).release();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof ApplicationStartedEvent){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            String zookeeperIp = PropertiesUtil.getProperty("zookeeper_ip");
            if(StringUtils.isEmpty(zookeeperIp)){
                zookeeperIp = "127.0.0.1:2181";
            }
            this.client = CuratorFrameworkFactory.newClient(zookeeperIp, retryPolicy);
            client.start();
        }
    }

}
