package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.service.mq.MqTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static org.seckill.api.enums.SeckillSolutionEnum.SYCHRONIZED;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class SynchronizedLockStrategy implements GoodsKillStrategy {
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    ExtSeckillMapper extSeckillMapper;
    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Autowired
    MqTask mqTask;

    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        int executeTime = requestDto.getCount();
        long seckillId = requestDto.getSeckillId();
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
                            Seckill sendTopicResult = new Seckill();
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
}
