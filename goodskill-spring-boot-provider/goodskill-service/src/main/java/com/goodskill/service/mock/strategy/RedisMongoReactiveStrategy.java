package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.common.constant.SeckillStatusConstant;
import com.goodskill.entity.Seckill;
import com.goodskill.mongo.vo.SeckillMockSaveVo;
import com.goodskill.mp.dao.mapper.SeckillMapper;
import com.goodskill.service.common.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

import static com.goodskill.common.enums.SeckillSolutionEnum.REDIS_MONGO_REACTIVE;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME_MONGO_SAVE;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class RedisMongoReactiveStrategy implements GoodsKillStrategy {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Resource
    private SeckillMapper extSeckillMapper;
    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        long seckillId = requestDto.getSeckillId();
        Seckill seckill = redisService.getSeckill(seckillId);
        if (redisTemplate.opsForValue().increment(seckillId) <= seckill.getNumber()) {
            taskExecutor.execute(() ->
                    streamBridge.send(DEFAULT_BINDING_NAME_MONGO_SAVE, MessageBuilder.withPayload(
                            SeckillMockSaveVo
                                    .builder()
                                    .seckillId(seckillId)
                                    .userPhone(requestDto.getPhoneNumber())
                                    .note(REDIS_MONGO_REACTIVE.getName())
                                    .build())
                            .build())
            );
        } else {
            synchronized (this) {
                seckill = redisService.getSeckill(seckillId);
                if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                    log.info("秒杀商品暂无库存，发送活动结束消息！");
                    streamBridge.send(DEFAULT_BINDING_NAME, MessageBuilder.withPayload(
                            SeckillMockResponseDTO
                                    .builder()
                                    .status(true)
                                    .seckillId(seckillId)
                                    .note(REDIS_MONGO_REACTIVE.getName())
                                    .taskId(requestDto.getTaskId())
                                    .build())
                            .build());

                    Seckill sendTopicResult = new Seckill();
                    sendTopicResult.setSeckillId(seckillId);
                    sendTopicResult.setStatus(SeckillStatusConstant.END);
                    extSeckillMapper.updateById(sendTopicResult);
                    seckill.setStatus(SeckillStatusConstant.END);
                    redisService.putSeckill(seckill);
                }
            }
        }
    }
}
