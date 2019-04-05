package org.seckill.service.impl;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.seckill.api.constant.SeckillSolutionEnum;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.api.dto.SeckillResult;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.exception.SeckillException;
import org.seckill.api.service.SeckillService;
import org.seckill.dao.GoodsMapper;
import org.seckill.dao.SeckillMapper;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.*;
import org.seckill.service.common.RedisService;
import org.seckill.service.common.trade.alipay.AlipayRunner;
import org.seckill.service.inner.SeckillExecutor;
import org.seckill.service.mq.MqTask;
import org.seckill.service.util.ZookeeperLockUtil;
import org.seckill.util.common.util.DateUtil;
import org.seckill.util.common.util.MD5Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.Message;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static org.seckill.api.constant.SeckillSolutionEnum.*;


/**
 * 秒杀接口实现类
 *
 * @author heng
 * @date 2016/7/16
 */
@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Slf4j
public class SeckillServiceImpl extends AbstractServiceImpl<SeckillMapper, SeckillExample, Seckill> implements SeckillService, InitializingBean {
    @Autowired
    private AlipayRunner alipayRunner;
    @Autowired
    private ExtSeckillMapper extSeckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private MqTask mqTask;
    @Autowired
    private ZookeeperLockUtil zookeeperLockUtil;
    @Value("${cache_ip_address}")
    private String cache_ip_address;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    SeckillExecutor seckillExecutor;
    @Autowired
    JmsTemplate jmsTemplate;

    private RedissonClient redissonClient;

    public void setAlipayRunner(AlipayRunner alipayRunner) {
        this.alipayRunner = alipayRunner;
    }

    public void setExtSeckillMapper(ExtSeckillMapper extSeckillMapper) {
        this.extSeckillMapper = extSeckillMapper;
    }

    public void setSuccessKilledMapper(SuccessKilledMapper successKilledMapper) {
        this.successKilledMapper = successKilledMapper;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void setGoodsMapper(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public PageInfo getSeckillList(int pageNum, int pageSize) {
        return selectByPage(new SeckillExample(), pageNum, pageSize);
    }

    @Override
    public SeckillInfo getById(long seckillId) {
        Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
        SeckillInfo seckillInfo = new SeckillInfo();
        BeanUtils.copyProperties(seckill, seckillInfo);
        Goods goods = goodsMapper.selectByPrimaryKey(seckill.getGoodsId());
        seckillInfo.setGoodsName(goods.getName());
        return seckillInfo;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //从redis中获取缓存秒杀信息
        Seckill seckill = redisService.getSeckill(seckillId);
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = MD5Util.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }


    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, String userPhone, String md5) {
        if (md5 == null || !md5.equals(MD5Util.getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = DateUtil.getNowTime();
        try {
            int updateCount = extSeckillMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                SuccessKilled successKilled = new SuccessKilled();
                successKilled.setSeckillId(seckillId);
                successKilled.setUserPhone(userPhone);
                int insertCount = successKilledMapper.insertSelective(successKilled);
                String QRfilePath = alipayRunner.trade_precreate(seckillId);
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilledKey key = new SuccessKilledKey();
                    key.setSeckillId(seckillId);
                    key.setUserPhone(userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilledMapper.selectByPrimaryKey(key), QRfilePath);
                }
            }
        } catch (SeckillCloseException e1) {
            log.info(e1.getMessage(), e1);
            throw e1;
        } catch (RepeatKillException e2) {
            log.info(e2.getMessage(), e2);
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public int addSeckill(Seckill seckill) {
        return extSeckillMapper.insert(seckill);
    }

    @Override
    public int deleteSeckill(Long seckillId) {
        return extSeckillMapper.deleteByPrimaryKey(seckillId);
    }

    @Override
    public int updateSeckill(Seckill seckill) {
        return extSeckillMapper.updateByPrimaryKeySelective(seckill);
    }

    @Override
    public Seckill selectById(Long seckillId) {
        return extSeckillMapper.selectByPrimaryKey(seckillId);
    }

    @Override
    public int deleteSuccessKillRecord(long seckillId) {
        SuccessKilledExample example = new SuccessKilledExample();
        example.createCriteria().andSeckillIdEqualTo(seckillId);
        return successKilledMapper.deleteByExample(example);
    }

    @Override
    public void executeWithSynchronized(Long seckillId, int executeTime) {
        CountDownLatch countDownLatch = new CountDownLatch(executeTime);
        for (int i = 0; i < executeTime; i++) {
            int userId = i;
            taskExecutor.execute(() -> {
                synchronized (this) {
                    Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);
                    if (seckill.getNumber() > 0) {
                        extSeckillMapper.reduceNumber(seckillId, new Date());
                        SuccessKilled record = new SuccessKilled();
                        record.setSeckillId(seckillId);
                        record.setUserPhone(String.valueOf(userId));
                        record.setStatus((byte) 1);
                        record.setCreateTime(new Date());
                        successKilledMapper.insert(record);
                    } else {
                        if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                            mqTask.sendSeckillSuccessTopic(seckillId, SYCHRONIZED.getName());
                            Seckill sendTopicResult = Seckill.builder().build();
                            sendTopicResult.setSeckillId(seckillId);
                            sendTopicResult.setStatus(SeckillStatusConstant.END);
                            extSeckillMapper.updateByPrimaryKeySelective(sendTopicResult);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("库存不足，无法继续秒杀！");
                        }
                    }
                }
                countDownLatch.countDown();
            });
        }
        // 等待线程执行完毕，阻塞当前进程
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void executeWithProcedure(Long seckillId, int executeTime, int userPhone) {
        seckillExecutor.dealSeckill(seckillId, String.valueOf(userPhone), SQL_PROCEDURE.getName());
    }

    @Override
    public void executeWithRedisson(Long seckillId, int executeTime, int userPhone) {
        RLock lock = redissonClient.getLock(seckillId + "");
        lock.lock();
        try {
            seckillExecutor.dealSeckill(seckillId, String.valueOf(userPhone), REDISSION_LOCK.getName());
        } finally {
            lock.unlock();
        }
    }


    /**
     * 获取秒杀成功笔数
     *
     * @param seckillId 秒杀活动id
     * @return
     */
    @Override
    public long getSuccessKillCount(Long seckillId) {
        SuccessKilledExample example = new SuccessKilledExample();
        example.createCriteria().andSeckillIdEqualTo(seckillId);
        long count = successKilledMapper.countByExample(example);
        return count;
    }

    @Override
    public void executeWithZookeeperLock(Long seckillId, int executeTime, int userPhone) {
        zookeeperLockUtil.lock(seckillId);
        try {
            seckillExecutor.dealSeckill(seckillId, String.valueOf(userPhone), ZOOKEEPER_LOCK.getName());
        } finally {
            zookeeperLockUtil.releaseLock(seckillId);
        }
    }

    @Override
    public void prepareSeckill(Long seckillId, int seckillCount) {
        // 初始化库存数量
        Seckill entity = Seckill.builder().build();
        entity.setSeckillId(seckillId);
        entity.setNumber(seckillCount);
        entity.setStatus(SeckillStatusConstant.IN_PROGRESS);
        this.updateByPrimaryKeySelective(entity);
        // 清理已成功秒杀记录
        this.deleteSuccessKillRecord(seckillId);
        Seckill seckill = redisService.getSeckill(seckillId);
        redisTemplate.opsForValue().increment(seckillId);
        while (redisTemplate.opsForValue().decrement(seckillId) > 1) {
            redisTemplate.opsForValue().decrement(seckillId);
        }
        seckill.setStatus(SeckillStatusConstant.IN_PROGRESS);
        redisService.putSeckill(seckill);
    }

    @Override
    public SeckillResult dealSeckill(Seckill seckill, SeckillSolutionEnum seckillSolutionEnum) {
        Long seckillId = seckill.getSeckillId();
        switch (seckillSolutionEnum) {
            case REDIS_MONGO_REACTIVE:
                seckill = redisService.getSeckill(seckillId);
                if (redisTemplate.opsForValue().increment(seckillId) < seckill.getNumber()) {
                    taskExecutor.execute(() ->
                            jmsTemplate.send("GOODSKILL_MONGO_SENCE8", session -> {
                                Message message = session.createMessage();
                                message.setLongProperty("seckillId", seckillId);
                                message.setStringProperty("userPhone", String.valueOf(1));
                                message.setStringProperty("note", REDIS_MONGO_REACTIVE.getName());
                                return message;
                            })
                    );
                    log.info("已发送");
                } else {
                    synchronized (this) {
                        seckill = redisService.getSeckill(seckillId);
                        if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                            log.info("秒杀商品暂无库存，发送活动结束消息！");
                            mqTask.sendSeckillSuccessTopic(seckillId, seckillSolutionEnum.getName());
                            Seckill sendTopicResult = Seckill.builder().build();
                            sendTopicResult.setSeckillId(seckillId);
                            sendTopicResult.setStatus(SeckillStatusConstant.END);
                            extSeckillMapper.updateByPrimaryKeySelective(sendTopicResult);
                            seckill.setStatus(SeckillStatusConstant.END);
                            redisService.putSeckill(seckill);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        Config config = new Config();
        config.useSingleServer().setAddress(cache_ip_address);
        redissonClient = Redisson.create(config);
    }

}
