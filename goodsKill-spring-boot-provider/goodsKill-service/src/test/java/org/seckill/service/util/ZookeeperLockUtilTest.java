package org.seckill.service.util;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seckill.service.GoodsKillRpcServiceSimpleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsKillRpcServiceSimpleApplication.class)
@Ignore
class ZookeeperLockUtilTest {
    @Autowired
    private ZookeeperLockUtil zookeeperLockUtil;

    @Test
    void lock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                if(zookeeperLockUtil.lock(10000L)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zookeeperLockUtil.releaseLock(10000L);
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        Assert.assertTrue(countDownLatch.getCount() == 0);
    }

}