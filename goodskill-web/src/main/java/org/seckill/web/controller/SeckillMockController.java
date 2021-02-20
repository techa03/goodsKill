package org.seckill.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.dto.SeckillResult;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Seckill;
import org.seckill.web.dto.SeckillWebMockRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.seckill.api.enums.SeckillSolutionEnum.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 *
 * @author heng
 * @date 2018/09/02
 */
@Api(tags = "模拟秒杀场景(无需登录)")
@RestController
@Slf4j
@Validated
public class SeckillMockController {

    @DubboReference
    private SeckillService seckillService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private Source source;

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于单机模式，扩展性差。
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized")
    public SeckillResult doWithSychronized(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        prepareSeckill(seckillId, seckillCount);
        log.info(SYCHRONIZED.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.execute(new SeckillMockRequestDto(seckillId, requestCount, null), SYCHRONIZED.getCode());
        return SeckillResult.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * @param seckillId
     * @param seckillCount
     */
    private void prepareSeckill(Long seckillId, int seckillCount) {
        seckillService.prepareSeckill(seckillId, seckillCount);
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于分布式环境，但速度不如加同步锁。
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "秒杀场景二(redis分布式锁实现)", notes = "秒杀场景二(redis分布式锁实现)", httpMethod = "POST")
    @PostMapping("/redisson")
    public SeckillResult doWithRedissionLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info(REDISSION_LOCK.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1))),
                            REDISSION_LOCK.getCode())
            );
        }
        return SeckillResult.ok();
    }

    /**
     * 异步秒杀
     * 场景三：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度较快，处理时间稍慢于场景一。
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "秒杀场景三(activemq消息队列实现)")
    @PostMapping("/activemq")
    @Deprecated
    public SeckillResult doWithActiveMqMessage(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        return SeckillResult.ok();
    }

    /**
     * 异步秒杀
     * 场景四：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快，速度优于activemq。
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafka")
    public SeckillResult doWithKafkaMqMessage(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info(KAFKA_MQ.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                        int i1 = atomicInteger.incrementAndGet();
                        String key = String.valueOf(i1);
                        kafkaTemplate.send("goodskill-kafka", key, String.valueOf(seckillId));
                    }
            );
        }
        return SeckillResult.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 通过同步锁控制秒杀并发，秒杀过程使用存储过程（秒杀未完成阻塞主线程）
     * 场景五：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "秒杀场景五(数据库原子性更新update set num = num -1)")
    @PostMapping("/procedure")
    public SeckillResult doWithProcedure(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        prepareSeckill(seckillId, seckillCount);
        log.info(ATOMIC_UPDATE.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1))),
                            ATOMIC_UPDATE.getCode())
            );
        }
        return SeckillResult.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 执行单次秒杀动作，等待实时处理结果，30秒超时
     * 场景六：返回执行结果的秒杀,30秒超时,activeMq实现
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)")
    @RequestMapping(value = "/activemq/reply/{seckillId}", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @Deprecated
    public SeckillResult doWithActiveMqMessageWithReply(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "userPhone") String userPhone) {
        return SeckillResult.ok();
    }


    /**
     *
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "秒杀场景七(zookeeper分布式锁)")
    @RequestMapping(value = "/zookeeperLock", method = POST, produces = {
            "application/json;charset=UTF-8"})
    public SeckillResult doWithZookeeperLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        prepareSeckill(seckillId, seckillCount);
        log.info(ZOOKEEPER_LOCK.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1))),
                            ZOOKEEPER_LOCK.getCode())
            );
        }
        return SeckillResult.ok();
    }

    /**
     * 场景八：使用redis缓存执行库存-1操作，最后通过发送MQ完成数据落地（存入mongoDB）
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)")
    @RequestMapping(value = "/redisReactiveMongo", method = POST, produces = {
            "application/json;charset=UTF-8"})
    public SeckillResult redisReactiveMongo(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        prepareSeckill(seckillId, seckillCount);
        log.info(REDIS_MONGO_REACTIVE.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, requestCount, String.valueOf(atomicInteger.addAndGet(1))), REDIS_MONGO_REACTIVE.getCode())
            );
        }
        return SeckillResult.ok();
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于单机模式，扩展性差。
     *
     * @param seckillId 秒杀活动id
     */
//    @ApiOperation(value = "各方案性能对比（一键运行）")
//    @PostMapping("/benchmark/{seckillId}")
    public void benchmark(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                          @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
    }

    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "秒杀场景九(rabbitmq)")
    @PostMapping("/rabbitmq")
    public SeckillResult doWithRabbitmq(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info(RABBIT_MQ.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                        SeckillMockRequestDto payload = new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1)));
                        source.output().send(MessageBuilder.withPayload(payload).build());
                    }
            );
        }
        return SeckillResult.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "秒杀场景十(Sentinel限流+数据库原子性更新)")
    @PostMapping("/limit")
    public SeckillResult limit(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info(SENTINEL_LIMIT.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                seckillService.execute(new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1))),
                        SENTINEL_LIMIT.getCode());
                    }
            );
        }
        return SeckillResult.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

}
