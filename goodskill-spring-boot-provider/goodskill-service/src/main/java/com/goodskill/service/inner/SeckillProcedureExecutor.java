package com.goodskill.service.inner;

import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.api.service.SeckillService;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.entity.Seckill;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME;

/**
 * @author heng
 */
@Slf4j
@Service
public class SeckillProcedureExecutor implements SeckillExecutor {

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private StreamBridge streamBridge;

    /**
     * 发送秒杀成功通知次数
     */
    private AtomicInteger sendTopicTimes = new AtomicInteger(0);

    /**
     * 处理用户秒杀请求
     *
     * @param seckillId 秒杀活动id
     * @param userPhone
     * @param note      秒杀备注信息
     */
    @Override
    public void dealSeckill(long seckillId, String userPhone, String note) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setSeckillId(seckillId);
            successKilled.setUserPhone(userPhone);
            successKilled.setCreateTime(new Date());
            successKilled.setServerIp(localHost.getHostAddress() + ":" + localHost.getHostName());
            if (seckillService.reduceNumber(successKilled) < 1) {
                Seckill seckill = seckillMapper.selectById(seckillId);
                log.debug("#dealSeckill 当前库存：{}，秒杀活动id:{}，商品id:{}", seckill.getNumber(), seckill.getSeckillId(), seckill.getGoodsId());
                // 高并发时可能多次发送完成通知
                if (!SeckillStatusConstant.END.equals(seckill.getStatus()) && sendTopicTimes.getAndIncrement() == 0) {
                    streamBridge.send(DEFAULT_BINDING_NAME, MessageBuilder.withPayload(
                                    SeckillMockResponseDTO.builder().seckillId(seckillId).note(note).status(true).build())
                            .build());
                    log.info("#dealSeckill 商品已售罄，最新秒杀信息：{}", seckill);
                    Seckill sendTopicResult = new Seckill();
                    sendTopicResult.setSeckillId(seckillId);
                    sendTopicResult.setStatus(SeckillStatusConstant.END);
                    seckillMapper.updateById(sendTopicResult);
                    // 重置发送成功通知次数
                    sendTopicTimes.set(0);
                }
                if (seckill.getNumber() <= 0) {
                    log.debug("#dealSeckill 库存不足，无法继续秒杀！");
                }
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
    }

}