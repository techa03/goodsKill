package org.seckill.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.dto.SeckillResult;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Reference
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
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized/{seckillId}")
    public SeckillResult doWithSychronized(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                           @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
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
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "秒杀场景二(redis分布式锁实现)", notes = "秒杀场景二(redis分布式锁实现)", httpMethod = "POST")
    @PostMapping("/redisson/{seckillId}")
    public SeckillResult doWithRedissionLock(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                             @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
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
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "秒杀场景三(activemq消息队列实现)")
    @PostMapping("/activemq/{seckillId}")
    @Deprecated
    public SeckillResult doWithActiveMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                               @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        return SeckillResult.ok();
    }

    /**
     * 异步秒杀
     * 场景四：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快，速度优于activemq。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafkamq/{seckillId}")
    public SeckillResult doWithKafkaMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                              @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info(KAFKA_MQ.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> kafkaTemplate.send("goodsKill-kafka", String.valueOf(atomicInteger.incrementAndGet()), String.valueOf(seckillId))
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
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "秒杀场景五(存储过程实现)")
    @PostMapping("/procedure/{seckillId}")
    public SeckillResult doWithProcedure(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                         @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        prepareSeckill(seckillId, seckillCount);
        log.info(SQL_PROCEDURE.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, 1, String.valueOf(atomicInteger.addAndGet(1))),
                            SQL_PROCEDURE.getCode())
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
    @ResponseBody
    @Deprecated
    public SeckillResult doWithActiveMqMessageWithReply(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "userPhone") String userPhone) {
        return SeckillResult.ok();
    }


    /**
     *
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "秒杀场景七(zookeeper分布式锁)")
    @RequestMapping(value = "/zookeeperLock/{seckillId}", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult doWithZookeeperLock(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                             @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) {
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
    @RequestMapping(value = "/redisReactiveMongo/{seckillId}", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult redisReactiveMongo(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                            @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) {
        prepareSeckill(seckillId, seckillCount);
        log.info(REDIS_MONGO_REACTIVE.getName() + "开始时间：{},秒杀id：{}", new Date(), seckillId);
        Seckill seckill = new Seckill();
        seckill.setSeckillId(seckillId);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() ->
                    seckillService.execute(new SeckillMockRequestDto(seckillId, requestCount, "123"), REDIS_MONGO_REACTIVE.getCode())
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
        doWithSychronized(seckillId, seckillCount, requestCount);
        doWithActiveMqMessage(seckillId, seckillCount, requestCount);
        doWithProcedure(seckillId, seckillCount, requestCount);
        doWithRedissionLock(seckillId, seckillCount, requestCount);
        doWithSychronized(seckillId, seckillCount, requestCount);
    }

    /**
     * @param seckillId 秒杀活动id
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "秒杀场景九(rabbitmq)")
    @PostMapping("/rabbitmq/{seckillId}")
    public SeckillResult doWithRabbitmq(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                        @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) {
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

}
