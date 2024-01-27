package com.goodskill.service.mock.strategy;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.common.core.constant.SeckillStatusConstant;
import com.goodskill.common.core.enums.Events;
import com.goodskill.order.vo.SeckillMockSaveVo;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.util.StateMachineUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

import static com.goodskill.common.core.enums.SeckillSolutionEnum.REDIS_MONGO_REACTIVE;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME_MONGO_SAVE;

/**
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class RedisMongoReactiveStrategy implements GoodsKillStrategy {
    @Resource
    private RedisService redisService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Resource
    private StreamBridge streamBridge;
    @Resource
    private StateMachineUtil stateMachineUtil;

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
                if (stateMachineUtil.feedMachine(Events.ACTIVITY_CALCULATE, seckillId)) {
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
                    seckill.setStatus(SeckillStatusConstant.END);
                    redisService.putSeckill(seckill);
                }
            }
        }
    }
}
