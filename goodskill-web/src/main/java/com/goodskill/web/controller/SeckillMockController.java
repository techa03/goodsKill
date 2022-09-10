package com.goodskill.web.controller;

import com.alibaba.fastjson2.JSON;
import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.service.SeckillService;
import com.goodskill.common.enums.SeckillSolutionEnum;
import com.goodskill.common.exception.CommonException;
import com.goodskill.common.info.Result;
import com.goodskill.web.dto.SeckillWebMockRequestDTO;
import com.goodskill.web.util.TaskTimeCaculateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.goodskill.common.enums.SeckillSolutionEnum.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 *
 * @author heng
 * @date 2018/09/02
 */
@Tag(name = "模拟秒杀场景(无需登录)")
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
    private StreamBridge streamBridge;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用于生成秒杀用户id
     */
    private final AtomicInteger SECKILL_PHONE_NUM_COUNTER = new AtomicInteger(0);

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于单机模式，扩展性差。
     */
    @Operation(summary = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized")
    @Async
    public Result doWithSychronized(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, SYCHRONIZED);
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景二：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于分布式环境，但速度不如加同步锁。
     */
    @Operation(summary = "秒杀场景二(redis分布式锁实现)", description = "秒杀场景二(redis分布式锁实现)", method = "POST")
    @PostMapping("/redisson")
    @Async
    public Result doWithRedissionLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, REDISSION_LOCK);
        return Result.ok();
    }

    /**
     * 异步秒杀
     * 场景三：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度较快，处理时间稍慢于场景一。
     */
    @Operation(summary = "秒杀场景三(activemq消息队列实现)")
    @PostMapping("/activemq")
    @Deprecated
    public Result doWithActiveMqMessage(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        return Result.ok();
    }

    /**
     * 异步秒杀
     * 场景四：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快，速度优于activemq。
     */
    @Operation(summary = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafka")
    @Async
    public Result doWithKafkaMqMessage(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, KAFKA_MQ, () -> {
            String phoneNumber = String.valueOf(SECKILL_PHONE_NUM_COUNTER.incrementAndGet());
            String taskId = String.valueOf(stringRedisTemplate.opsForValue().get("SECKILL_TASK_ID_COUNTER"));
            SeckillMockRequestDTO payload = new SeckillMockRequestDTO(dto.getSeckillId(), 1, phoneNumber, taskId);
            kafkaTemplate.send("goodskill-kafka", phoneNumber, JSON.toJSONString(payload));
        });
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 通过同步锁控制秒杀并发，秒杀过程使用存储过程（秒杀未完成阻塞主线程）
     * 场景五：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快
     */
    @Operation(summary = "秒杀场景五(数据库原子性更新update set num = num -1)")
    @PostMapping("/procedure")
    @Async
    public Result doWithProcedure(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, ATOMIC_UPDATE);
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 执行单次秒杀动作，等待实时处理结果，30秒超时
     * 场景六：返回执行结果的秒杀,30秒超时,activeMq实现
     *
     * @param seckillId 秒杀活动id
     */
    @Operation(summary = "秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)")
    @RequestMapping(value = "/activemq/reply/{seckillId}", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @Deprecated
    public Result doWithActiveMqMessageWithReply(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "userPhone") String userPhone) {
        return Result.ok();
    }


    /**
     *
     */
    @Operation(summary = "秒杀场景七(zookeeper分布式锁)")
    @RequestMapping(value = "/zookeeperLock", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @Async
    public Result doWithZookeeperLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, ZOOKEEPER_LOCK);
        return Result.ok();
    }

    /**
     * 场景八：使用redis缓存执行库存-1操作，最后通过发送MQ完成数据落地（存入mongoDB）
     */
    @Operation(summary = "秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)")
    @RequestMapping(value = "/redisReactiveMongo", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @Async
    public Result redisReactiveMongo(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, REDIS_MONGO_REACTIVE);
        return Result.ok();
    }

    @Operation(summary = "秒杀场景九(rabbitmq)")
    @PostMapping("/rabbitmq")
    @Async
    public Result doWithRabbitmq(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, RABBIT_MQ, () -> {
            String phoneNumber = String.valueOf(SECKILL_PHONE_NUM_COUNTER.incrementAndGet());
            String taskId = String.valueOf(stringRedisTemplate.opsForValue().get("SECKILL_TASK_ID_COUNTER"));
            SeckillMockRequestDTO payload = new SeckillMockRequestDTO(dto.getSeckillId(), 1, phoneNumber, taskId);
            streamBridge.send("seckill-out-0", payload);
        });
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    @Operation(summary = "秒杀场景十(Sentinel限流+数据库原子性更新)")
    @PostMapping("/limit")
    @Async
    public Result limit(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, SENTINEL_LIMIT);
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * canal测试方法说明：启动goodskill-canal模块下的CanalClientApplication类即可，canal使用默认配置。注意要先启动canal-server，并使用tcp模式
     * 秒杀结束后会在控台输出日志
     *
     * @param dto 秒杀活动
     * @see com.goodskill.web.stream.consumer.SeckillMockCanalResponseListener 为对应的消息接受者
     */
    @Operation(summary = "秒杀场景十一(数据库原子性更新+canal 数据库binlog日志监听秒杀结果)")
    @PostMapping("/atomicWithCanal")
    @Async
    public Result atomicWithCanal(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        processSeckill(dto, ATOMIC_CANAL);
        return Result.ok();
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 准备商品库存
     *
     * @param seckillId
     * @param seckillCount
     * @param name
     * @param taskId
     */
    private void prepareSeckill(long seckillId, int seckillCount, String name, String taskId) {
        seckillService.prepareSeckill(seckillId, seckillCount, taskId);
        TaskTimeCaculateUtil.startTask("秒杀活动id:" + seckillId + "," + name, taskId);
    }

    /**
     * 变更线程池参数
     *
     * @param dto 参数
     */
    private void changeThreadPoolParam(SeckillWebMockRequestDTO dto) {
        try {
            if (dto.getCorePoolSize() != null && dto.getCorePoolSize() > 0) {
                int corePoolSize = taskExecutor.getCorePoolSize();
                taskExecutor.setCorePoolSize(dto.getCorePoolSize());
                log.info("#changeThreadPoolParam 更新核心线程数参数生效, 原参数值:{},当前值:{}", corePoolSize, dto.getCorePoolSize());
            }
            if (dto.getMaxPoolSize() != null && dto.getMaxPoolSize() > 0) {
                int maxPoolSize = taskExecutor.getMaxPoolSize();
                taskExecutor.setMaxPoolSize(dto.getMaxPoolSize());
                log.info("#changeThreadPoolParam 更新最大线程数参数生效, 原参数值:{},当前值:{}", maxPoolSize, dto.getMaxPoolSize());
            }
        } catch (IllegalArgumentException e) {
            log.warn("#changeThreadPoolParam 核心线程数不能大于最大线程数，当前最大线程数:{}，当前核心线程数:{}", taskExecutor.getMaxPoolSize(), taskExecutor.getCorePoolSize(), e);
            throw new CommonException("线程池参数不合法，请重新设置！");
        }
    }

    /**
     * 执行秒杀程序
     *
     * @param dto                 秒杀请求
     * @param seckillSolutionEnum 秒杀策略
     */
    private void processSeckill(SeckillWebMockRequestDTO dto, SeckillSolutionEnum seckillSolutionEnum) {
        processSeckill(dto, seckillSolutionEnum, null);
    }

    /**
     * 执行秒杀程序
     *
     * @param dto                 秒杀请求
     * @param seckillSolutionEnum 秒杀策略
     * @param runnable            待执行的任务
     */
    private void processSeckill(SeckillWebMockRequestDTO dto, SeckillSolutionEnum seckillSolutionEnum, Runnable runnable) {
        log.debug("#processSeckill start count:{},当前线程池队列长度:{},线程数:{},是否空:{}", SECKILL_PHONE_NUM_COUNTER.get(),
                taskExecutor.getThreadPoolExecutor().getQueue().size(),
                taskExecutor.getPoolSize(), taskExecutor.getThreadPoolExecutor().getQueue().isEmpty());
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        Long seckillTaskIdCounter = stringRedisTemplate.opsForValue().increment("SECKILL_TASK_ID_COUNTER");
        String taskId = String.valueOf(seckillTaskIdCounter);
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount, seckillSolutionEnum.getName(), taskId);
        changeThreadPoolParam(dto);
        log.info(seckillSolutionEnum.getName() + "开始时间:{}, 秒杀id:{}, 任务Id:{}", new Date(), seckillId, taskId);
        if (runnable == null) {
            // 默认的执行方法
            runnable = () -> {
                String phoneNumber = String.valueOf(SECKILL_PHONE_NUM_COUNTER.incrementAndGet());
                seckillService.execute(new SeckillMockRequestDTO(seckillId, 1, phoneNumber, taskId),
                        seckillSolutionEnum.getCode());
            };
        }
        for (int i = 0; i < requestCount; i++) {
            if (log.isDebugEnabled()) {
                log.debug("#processSeckill begin count:{},当前线程池队列长度:{},线程数:{},是否空:{}", SECKILL_PHONE_NUM_COUNTER.get(),
                        taskExecutor.getThreadPoolExecutor().getQueue().size(),
                        taskExecutor.getPoolSize(), taskExecutor.getThreadPoolExecutor().getQueue().isEmpty());
            }
            taskExecutor.execute(runnable);
        }
    }

}
