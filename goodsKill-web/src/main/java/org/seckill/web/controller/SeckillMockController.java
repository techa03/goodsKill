package org.seckill.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.seckill.entity.Seckill;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 *
 * @author heng
 * @date 2018/09/02
 */
@Api(tags = "模拟秒杀场景(无需登录)")
@RestController("/seckill")
@Slf4j
public class SeckillMockController {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于单机模式，扩展性差。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized/{seckillId}")
    public void doWithSychronized(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                  @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景一(sychronized同步锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
        seckillService.executeWithSynchronized(seckillId, requestCount);
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * @param seckillId
     * @param seckillCount
     */
    private void prepareSeckill(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount) {
        // 初始化库存数量
        Seckill entity = new Seckill();
        entity.setSeckillId(seckillId);
        entity.setNumber(seckillCount);
        entity.setStatus(SeckillStatusConstant.IN_PROGRESS);
        seckillService.updateByPrimaryKeySelective(entity);
        // 清理已成功秒杀记录
        seckillService.deleteSuccessKillRecord(seckillId);
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于分布式环境，但速度不如加同步锁。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景二(redis分布式锁实现)")
    @PostMapping("/redisson/{seckillId}")
    public void doWithRedissionLock(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                    @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景二(redis分布式锁实现)时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                seckillService.executeWithRedisson(seckillId, 1, atomicInteger.addAndGet(1));
            });
        }
    }

    /**
     * 异步秒杀
     * 场景三：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度较快，处理时间稍慢于场景一。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景三(activemq消息队列实现)")
    @PostMapping("/activemq/{seckillId}")
    public void doWithActiveMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                      @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景三(activemq消息队列实现)时间：{},秒杀id：{}", new Date(), seckillId);
        // 保证用户id不重复
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                jmsTemplate.send((Session session) -> {
                    Message message = session.createMessage();
                    message.setLongProperty("seckillId", seckillId);
                    message.setStringProperty("userPhone", String.valueOf(atomicInteger.incrementAndGet()));
                    message.setStringProperty("note", String.valueOf("秒杀场景三(activemq消息队列实现)"));
                    return message;
                });
            });
        }
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 异步秒杀
     * 场景四：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：速度快，速度优于activemq。
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景四(kafka消息队列实现)")
    @PostMapping("/kafkamq/{seckillId}")
    public void doWithKafkaMqMessage(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                     @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        // 初始化库存数量
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景四(kafka消息队列实现)时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        kafkaTemplate.sendDefault(atomicInteger.incrementAndGet(), String.valueOf(seckillId));
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                kafkaTemplate.sendDefault(atomicInteger.incrementAndGet(), String.valueOf(seckillId));
            });
        }
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
    @ApiOperation(value = "秒杀场景五(存储过程实现)")
    @PostMapping("/procedure/{seckillId}")
    public void doWithProcedure(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) throws InterruptedException {
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景五(存储过程实现)时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                seckillService.executeWithProcedure(seckillId, 1, atomicInteger.addAndGet(1));
            });
        }
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 执行单次秒杀动作，等待实时处理结果，30秒超时
     * 场景六：返回执行结果的秒杀,30秒超时,activeMq实现
     *
     * @param seckillId 秒杀活动id
     */
    @ApiOperation(value = "秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)")
    @PostMapping("/activemq/reply/{seckillId}")
    @ResponseBody
    public String doWithActiveMqMessageWithReply(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "userPhone") String userPhone) {
        prepareSeckill(seckillId, 10);
        log.info("秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)开始时间：{},秒杀id：{}", new Date(), seckillId);

        Message mes = jmsTemplate.sendAndReceive(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createMessage();
                message.setLongProperty("seckillId", seckillId);
                message.setStringProperty("userPhone", userPhone);
                // 指定服务方应答到临时队列中，请求方最终从临时队列获取消息
                message.setJMSReplyTo(session.createTemporaryQueue());
                // 消息超时时间设置为5分钟
                message.setJMSExpiration(5 * 60 * 1000);
                message.setJMSCorrelationID(UUID.randomUUID().toString());
                return message;
            }
        });
        String result = "";
        try {
            result = mes.getStringProperty("message");
        } catch (JMSException e) {
            log.warn(e.getMessage(), e);
        }
        log.info("秒杀场景六(返回执行结果的秒杀,30秒超时,activeMq实现)结束时间：{},秒杀id：{}", new Date(), seckillId);
        return result;
    }


    @PostMapping("/zookeeperLock/{seckillId}")
    @ResponseBody
    public void doWithZookeeperLock(@PathVariable("seckillId") Long seckillId, @RequestParam(name = "seckillCount", required = false, defaultValue = "1000") int seckillCount,
                                      @RequestParam(name = "requestCount", required = false, defaultValue = "2000") int requestCount) {
        prepareSeckill(seckillId, seckillCount);
        log.info("秒杀活动开始，秒杀场景七(zookeeper分布式锁)时间：{},秒杀id：{}", new Date(), seckillId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < requestCount; i++) {
            taskExecutor.execute(() -> {
                seckillService.executeWithZookeeperLock(seckillId, 1, atomicInteger.addAndGet(1));
            });
        }
    }

}
