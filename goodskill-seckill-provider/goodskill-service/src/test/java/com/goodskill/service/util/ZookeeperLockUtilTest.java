package com.goodskill.service.util;

import com.goodskill.service.GoodsKillServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GoodsKillServiceApplication.class)
@Disabled
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
        assertTrue(countDownLatch.getCount() == 0);
    }

}