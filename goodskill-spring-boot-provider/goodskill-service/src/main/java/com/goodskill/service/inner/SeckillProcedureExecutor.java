package com.goodskill.service.inner;

import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.api.service.SeckillService;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.entity.Seckill;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.service.common.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

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
    @Autowired
    private RedisService redisService;

    /**
     * 处理用户秒杀请求
     *
     * @param seckillId 秒杀活动id
     * @param userPhone 秒杀用户手机号
     * @param note      秒杀备注信息
     * @param taskId    秒杀任务id
     */
    @Override
    public void dealSeckill(long seckillId, String userPhone, String note, String taskId) {
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
                if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                    // 高并发时可能多次发送完成通知，使用锁控制
                    Boolean endFlag = redisService.setSeckillEndFlag(seckillId, taskId);
                    if (endFlag) {
                        streamBridge.send(DEFAULT_BINDING_NAME, MessageBuilder.withPayload(
                                        SeckillMockResponseDTO.builder().seckillId(seckillId).note(note).status(true).taskId(taskId).build())
                                .build());
                        log.info("#dealSeckill 商品已售罄，最新秒杀信息：{}", seckill);
                        Seckill sendTopicResult = new Seckill();
                        sendTopicResult.setSeckillId(seckillId);
                        sendTopicResult.setStatus(SeckillStatusConstant.END);
                        seckillMapper.updateById(sendTopicResult);
                    }
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