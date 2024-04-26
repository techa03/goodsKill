package com.goodskill.service.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * 采用curator提供的互斥锁获取锁方法
     * 采用线程本地变量的方法解决并发问题
     *
     * @param seckillId 秒杀id
     * @return true 加锁成功
     */
    public InterProcessMutex lock(long seckillId) {
        try {
            String rootLockPath = "/goodskill";
            InterProcessMutex interProcessMutex = new InterProcessMutex(client, rootLockPath + "/" + seckillId);
            boolean acquire = interProcessMutex.acquire(1000L, TimeUnit.MILLISECONDS);
            if (acquire) {
                log.info("成功获取到zk锁,秒杀id{}", seckillId);
            } else {
                log.info("未获取到zk锁,秒杀id{}", seckillId);
            }
            return interProcessMutex;
        } catch (Exception e) {
            log.warn("获取zk锁异常:{}", e.getMessage());
            return null;
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
        client.close();
        log.info("zkClient已关闭");
    }

    private ZookeeperLockUtil() {
    }

}
