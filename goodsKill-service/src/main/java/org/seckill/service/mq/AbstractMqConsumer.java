package org.seckill.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.seckill.dao.SuccessKilledMapper;
import org.seckill.dao.ext.ExtSeckillMapper;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.service.inner.SeckillExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 *
 */
@Slf4j
public class AbstractMqConsumer implements SeckillExecutor {
    @Autowired
    private ExtSeckillMapper extSeckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;
    @Autowired
    private MqTask mqTask;

    /**
     * 处理用户秒杀请求
     *
     * @param seckillId 秒杀活动id
     * @param userPhone
     * @param note      秒杀备注信息
     */
    @Override
    public void dealSeckill(long seckillId, String userPhone, String note) {
        Seckill seckill = extSeckillMapper.selectByPrimaryKey(seckillId);

        log.info("当前库存：{}", seckill.getNumber());
        if (seckill.getNumber() > 0) {
            //存储过程实现，适用于分布式环境
            extSeckillMapper.reduceNumberByProcedure(seckillId, Long.valueOf(userPhone), new Date());
            SuccessKilled record = new SuccessKilled();
            record.setSeckillId(seckillId);
            record.setUserPhone(userPhone);
            record.setStatus((byte) 1);
            record.setCreateTime(new Date());
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                record.setServerIp(localHost.getHostAddress() + ":" + localHost.getHostName());
            } catch (UnknownHostException e) {
                log.warn("请求被未知IP处理！", e);
            }
            successKilledMapper.insert(record);
        } else {
            if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                mqTask.sendSeckillSuccessTopic(seckillId, note);
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
}
