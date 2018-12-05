package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZookeeperLockUtil{

//    private CuratorFramework client;
//    private String ROOT_LOCK_PATH = "/goodsKill";
//    private ThreadLocal<Map<Long, InterProcessMutex>> threadLock = new ThreadLocal<>();
//
//    public boolean lock(long seckillId) {
//        try {
//            Map<Long, InterProcessMutex> map;
//            if (threadLock.get() == null) {
//                map = new HashMap();
//                map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
//                threadLock.set(map);
//            } else {
//                if (threadLock.get().get(seckillId) == null) {
//                    map = threadLock.get();
//                    map.put(seckillId, new InterProcessMutex(client, ROOT_LOCK_PATH + "/" + String.valueOf(seckillId)));
//                }
//                threadLock.get().get(seckillId).acquire(2L, TimeUnit.SECONDS);
//            }
//            return true;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
//    public boolean releaseLock(long seckillId) {
//        try {
//            threadLock.get().get(seckillId).release();
//            return true;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
////    @Override
//    public void afterPropertiesSet() {
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        this.client = CuratorFrameworkFactory.newClient(PropertiesUtil.getProperty("zookeeper_ip"), retryPolicy);
//        client.start();
//    }
}
