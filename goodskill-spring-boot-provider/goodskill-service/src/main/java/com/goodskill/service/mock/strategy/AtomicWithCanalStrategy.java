package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.entity.Seckill;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.mp.dao.mapper.SuccessKilledMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库原子性更新+canal 数据库binlog日志监听秒杀结果 秒杀策略
 *
 * @author techa03
 * @date 2022/5/14
 */
@Component
@Slf4j
public class AtomicWithCanalStrategy implements GoodsKillStrategy {

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;

    private final ConcurrentHashMap<Long, Object> seckillIdList = new ConcurrentHashMap<>();

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        Long seckillId = requestDto.getSeckillId();
        String userId = requestDto.getPhoneNumber();
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
                    Seckill sendTopicResult = new Seckill();
                    sendTopicResult.setSeckillId(seckillId);
                    sendTopicResult.setStatus(SeckillStatusConstant.END);
                    seckillMapper.updateById(sendTopicResult);
                }
                if (log.isDebugEnabled()) {
                    log.debug("#execute 库存不足，无法继续秒杀！");
                }
            }
        }
    }
}
