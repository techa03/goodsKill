package org.seckill.service.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seckill.service.GoodsKillRpcServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsKillRpcServiceApplication.class)
@Ignore
@Slf4j
class ZookeeperLockUtilTest {
    @Autowired
    private ZookeeperLockUtil zookeeperLockUtil;

    @Test
    void lock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int count = 200;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count/2; i++) {
            executorService.execute(() -> {
                if(zookeeperLockUtil.lock(10000L)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zookeeperLockUtil.releaseLock(10000L);
                    log.debug("计数器当前值{}", countDownLatch.getCount());
                    countDownLatch.countDown();
                }
            });
            executorService.execute(() -> {
                if(zookeeperLockUtil.lock(10001L)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zookeeperLockUtil.releaseLock(10001L);
                    log.debug("计数器当前值{}", countDownLatch.getCount());
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Assert.assertTrue(countDownLatch.getCount() == 0);
    }

}