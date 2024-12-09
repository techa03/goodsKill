package com.goodskill.ai.service.feign;

import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 模拟秒杀场景
 *
 * @author heng
 * @date 2024/12/02
 */
@FeignClient(name = "goodskill-web")
public interface SeckillMockFeignClient {

    @Operation(summary = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized")
    Result doWithSychronized(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景二(redis分布式锁实现)", description = "秒杀场景二(redis分布式锁实现)", method = "POST")
    @PostMapping("/redisson")
    Result doWithRedissionLock(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafka")
    Result doWithKafkaMqMessage(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景五(数据库原子性更新update set num = num -1)")
    @PostMapping("/procedure")
    Result<Long> doWithProcedure(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景七(zookeeper分布式锁)")
    @RequestMapping(value = "/zookeeperLock", method = POST, produces = {
            "application/json;charset=UTF-8"})
    Result doWithZookeeperLock(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)")
    @RequestMapping(value = "/redisReactiveMongo", method = POST, produces = {
            "application/json;charset=UTF-8"})
    Result redisReactiveMongo(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景九(rabbitmq)")
    @PostMapping("/rabbitmq")
    Result doWithRabbitmq(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景十(Sentinel限流+数据库原子性更新)")
    @PostMapping("/limit")
    Result limit(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    @Operation(summary = "秒杀场景十一(数据库原子性更新+canal 数据库binlog日志监听秒杀结果)")
    @PostMapping("/atomicWithCanal")
    Result atomicWithCanal(@RequestBody @Valid SeckillWebMockRequestDTO dto);

    /**
     * 获取秒杀活动最新任务id
     *
     * @param seckillId 秒杀活动id
     * @return 秒杀活动最新任务id
     */
    @PostMapping("/task-info")
    Result<Long> getTaskId(@RequestParam Long seckillId);

    /**
     * 获取任务耗时统计信息
     *
     * @param seckillId 秒杀活动的ID
     * @return 返回一个Result对象，其中包含格式化后的任务时间信息字符串
     */
    @GetMapping("/task-time-info")
    Result<String> getTaskTimeInfo(@RequestParam Long seckillId);

}
