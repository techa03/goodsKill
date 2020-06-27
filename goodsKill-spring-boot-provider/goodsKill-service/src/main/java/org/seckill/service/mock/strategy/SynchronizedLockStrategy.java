package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.mq.ActiveMqMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import static org.seckill.api.enums.SeckillSolutionEnum.SYCHRONIZED;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class SynchronizedLockStrategy implements GoodsKillStrategy {
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private ActiveMqMessageSender activeMqMessageSender;
    private final ConcurrentHashMap<Long, Object> seckillIdList = new ConcurrentHashMap<>();

    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        int executeTime = requestDto.getCount();
        long seckillId = requestDto.getSeckillId();
        CountDownLatch countDownLatch = new CountDownLatch(executeTime);
        for (int i = 0; i < executeTime; i++) {
            int userId = i;
            taskExecutor.execute(() -> {
                // 此处已优化，锁单个商品而不是整个方法
                Object seckillMonitor = seckillIdList.get(seckillId);
                if (seckillMonitor == null) {
                    Object value = new Object();
                    seckillIdList.put(seckillId, value);
                }
                synchronized (seckillIdList.get(seckillId)) {
                    Seckill seckill = seckillMapper.selectById(seckillId);
                    if (seckill.getNumber() > 0) {
                        seckillMapper.reduceNumber(seckillId, new Date());
                        SuccessKilled record = new SuccessKilled();
                        record.setSeckillId(seckillId);
                        record.setUserPhone(String.valueOf(userId));
                        record.setStatus(1);
                        record.setCreateTime(new Date());
                        successKilledMapper.insert(record);
                    } else {
                        if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                            activeMqMessageSender.sendSeckillSuccessTopic(seckillId, SYCHRONIZED.getName());
                            Seckill sendTopicResult = new Seckill();
                            sendTopicResult.setSeckillId(seckillId);
                            sendTopicResult.setStatus(SeckillStatusConstant.END);
                            seckillMapper.updateById(sendTopicResult);
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
            Thread.currentThread().interrupt();
        }
    }
}
