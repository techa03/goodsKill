package com.goodskill.service.test.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.service.SeckillService;
import com.goodskill.service.GoodsKillServiceApplication;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.entity.SuccessKilled;
import com.goodskill.service.mapper.SeckillMapper;
import com.goodskill.service.mapper.SuccessKilledMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by heng on 2017/6/28.
 */
@SpringBootTest(classes = GoodsKillServiceApplication.class)
@ExtendWith(SpringExtension.class)
@Disabled
@Transactional
public class BaseServiceConfigForTest {
    @Resource
    private SeckillService seckillService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SeckillMapper seckillMapper;
    @Resource
    private SuccessKilledMapper successKilledMapper;

    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private AtomicBoolean signal = new AtomicBoolean(false);
    private Map<Thread, BlockingQueue> taskQueue = new ConcurrentHashMap(100);
    private AtomicInteger updateCount = new AtomicInteger(0);
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition = reentrantLock.newCondition();

    @Test
    public void test() {
        Page seckillList = seckillService.getSeckillList(0, 10, "小");
        seckillList.getRecords().forEach(System.out::println);
        assertTrue(seckillList.getTotal() > 0);
    }

    @Test
    public void test1() throws InterruptedException {
//        redisTemplate.opsForValue().set("10", seckillMapper.selectById(1000L));

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue(50), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        new Thread(() -> {
            while (true) {
                threadPoolExecutor.execute(() -> {
                    try {
                        testSelect();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
        Thread.sleep(100L);
        testUpdate();
        atomicBoolean.set(true);
        System.out.println("更新完了！");
        Thread.sleep(100000L);
    }


    public Seckill testSelect() throws ExecutionException, InterruptedException {
        Seckill seckill = (Seckill) redisTemplate.opsForValue().get(1000);
        if (seckill == null) {
            Seckill value;
            if (signal.get()) {
                putIntoJvm();
                value = getFromJvm();
            } else {
                value = seckillMapper.selectById(1000);
                updateRedis(value);
            }
            if (atomicBoolean.get()) {
                System.out.println(Thread.currentThread().getName() + "已更新缓存：" + value.getNumber());
            }
            return value;
        } else {
            if (atomicBoolean.get()) {
                System.out.println(Thread.currentThread().getName() + "获取到缓存：" + seckill.getNumber());
            }
            return seckill;
        }
    }

    private Seckill getFromJvm() throws InterruptedException, ExecutionException {
        BlockingQueue blockingQueue = taskQueue.get(Thread.currentThread());
        Seckill seckill = null;
        while (!blockingQueue.isEmpty()) {
            Object object = blockingQueue.take();
            reentrantLock.lock();
            try {
                if (object instanceof Callable) {
                    FutureTask futureTask = new FutureTask((Callable) object);
                    while (signal.get()) {
                        condition.await();
                    }
                    futureTask.run();
                    seckill = (Seckill) futureTask.get();
                } else {
                    FutureTask futureTask = new FutureTask((Runnable) object, 1);
                    while (signal.get()) {
                        condition.await();
                    }
                    futureTask.run();
                    updateCount.decrementAndGet();
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        return seckill;
    }

    private void putIntoJvm() throws InterruptedException {
        BlockingQueue blockingQueue = taskQueue.get(Thread.currentThread());
        if (blockingQueue == null) {
            blockingQueue = new ArrayBlockingQueue(100);
            taskQueue.put(Thread.currentThread(), blockingQueue);
        }
        blockingQueue.offer((Callable<Seckill>) () -> {
            Seckill seckill = seckillMapper.selectById(1000);
            if (updateCount.getAndIncrement() == 0) {
                System.out.println("内存队列已更新缓存！！！！！");
                redisTemplate.opsForValue().set(1000, seckill);
                System.out.println("updateCount:" + updateCount.get());
            }
            return seckill;
        });
        System.out.println(Thread.currentThread().getName() + "已插入任务，当前任务数：" + blockingQueue.size());
    }


    private void updateRedis(Seckill value) {
        redisTemplate.opsForValue().set(1000, value);
    }

    public void testUpdate() throws InterruptedException {
        signal.set(true);
        redisTemplate.delete(1000);
        Seckill entity = new Seckill();
        entity.setSeckillId(1000L);
        entity.setNumber(1220);
        seckillMapper.updateById(entity);
        signal.set(false);
        condition.signalAll();
    }

//    @Test
    public void testInsertSuccessKilled() {
        SuccessKilled entity = new SuccessKilled();
        entity.setSeckillId(1L);
        entity.setUserPhone("2");
        entity.setStatus(0);
        entity.setCreateTime(new Date());
        entity.setServerIp("1");
        entity.setUserIp("1");
        entity.setUserId("1");
        successKilledMapper.insert(entity);
        entity.setSeckillId(1L);
        entity.setUserPhone("3");
        entity.setStatus(0);
        entity.setCreateTime(new Date());
        entity.setServerIp("1");
        entity.setUserIp("1");
        entity.setUserId("1");
        successKilledMapper.insert(entity);
        entity.setSeckillId(2L);
        entity.setUserPhone("2");
        entity.setStatus(0);
        entity.setCreateTime(new Date());
        entity.setServerIp("1");
        entity.setUserIp("1");
        entity.setUserId("1");
        successKilledMapper.insert(entity);
        entity.setSeckillId(2L);
        entity.setUserPhone("3");
        entity.setStatus(0);
        entity.setCreateTime(new Date());
        entity.setServerIp("1");
        entity.setUserIp("1");
        entity.setUserId("1");
        successKilledMapper.insert(entity);
    }


    @Test
    public void testInsertOrder() {
        assertTrue(successKilledMapper.selectList(null).size() > 0);
    }

    @BeforeEach
    public void before() {
        testInsertSuccessKilled();
    }
}
