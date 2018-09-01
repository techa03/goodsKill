package org.seckill.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.SeckillService;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 * @author heng
 * @date 2018/09/02
 */
@Api(tags = "模拟秒杀场景(无需登录)")
@RestController("/seckill")
@Slf4j
public class SeckillMockController {

    @Autowired
    SeckillService seckillService;

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以很好的解决秒杀问题，适用于单机模式，扩展性差。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized/{seckillId}")
    public void doWithSychronized(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                  @RequestParam(name = "requestCount", required = false, defaultValue = "2000")  int requestCount) throws InterruptedException {
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景一(sychronized同步锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.executeWithSynchronized(seckillId, requestCount);
        log.info("最终成功交易笔数：{}", seckillService.getSuccessKillCount(seckillId));
        log.info("秒杀活动结束，秒杀场景一(sychronized同步锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
    }

    /**
     *
     * @param seckillId
     * @param seckillCount
     */
    private void prepareSeckill(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount) {
        // 初始化库存数量
        Seckill entity = new Seckill();
        entity.setSeckillId(seckillId);
        entity.setNumber(seckillCount);
        seckillService.updateByPrimaryKeySelective(entity);
        // 清理已成功秒杀记录
        seckillService.deleteSuccessKillRecord(seckillId);
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以很好的解决秒杀问题，适用于分布式环境，但速度不如加同步锁。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景二(redis分布式锁实现)")
    @PostMapping("/redisson/{seckillId}")
    public void doWithRedissionLock(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                    @RequestParam(name = "requestCount", required = false, defaultValue = "2000")  int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景二(redis分布式锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.executeWithRedisson(seckillId, requestCount);
        log.info("最终成功交易笔数：{}", seckillService.getSuccessKillCount(seckillId));
        log.info("秒杀活动结束，秒杀场景二(redis分布式锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
    }

    /**
     * 异步秒杀
     * 场景三：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度较快，处理时间与场景一大致相同。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景三(activemq消息队列实现)")
    @PostMapping("/activemq/{seckillId}")
    public void doWithActiveMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                      @RequestParam(name = "requestCount", required = false, defaultValue = "2000")  int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景三(activemq消息队列实现)时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.executeWithActiveMq(seckillId, requestCount);
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 异步秒杀
     * 场景三：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快，速度优于activemq。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafkamq/{seckillId}")
    public void doWithKafkaMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                     @RequestParam(name = "requestCount", required = false, defaultValue = "2000")  int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景四(kafka消息队列实现)时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.executeWithKafkaMq(seckillId, requestCount);
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

}
