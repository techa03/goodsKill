package com.goodskill.service.util;

import com.goodskill.service.SeckillApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
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
@SpringBootTest(classes = SeckillApplication.class)
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
                InterProcessMutex lock = zookeeperLockUtil.lock(10000L);
                if(lock != null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        lock.release();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    log.debug("计数器当前值{}", countDownLatch.getCount());
                    countDownLatch.countDown();
                }
            });
            executorService.execute(() -> {
                InterProcessMutex lock = zookeeperLockUtil.lock(10001L);
                if(lock != null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        lock.release();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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
