package com.goodskill.service.mock.strategy.impl;

import com.goodskill.api.dto.SeckillMockRequestDTO;
import com.goodskill.api.dto.SeckillMockResponseDTO;
import com.goodskill.core.constant.SeckillStatusConstant;
import com.goodskill.core.enums.Events;
import com.goodskill.core.enums.States;
import com.goodskill.order.vo.SeckillMockSaveVo;
import com.goodskill.service.common.RedisService;
import com.goodskill.service.entity.Seckill;
import com.goodskill.service.mock.strategy.GoodsKillStrategy;
import com.goodskill.service.util.StateMachineService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import static com.goodskill.core.enums.SeckillSolutionEnum.REDIS_MONGO_REACTIVE;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME;
import static com.goodskill.service.common.constant.CommonConstant.DEFAULT_BINDING_NAME_MONGO_SAVE;

/**
 * Redis + MongoDB 响应式秒杀策略
 *
 * @author techa03
 * @date 2019/7/27
 */
@Component
@Slf4j
public class RedisMongoReactiveStrategy implements GoodsKillStrategy {
    @Resource
    private RedisService redisService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolExecutor taskExecutor;
    @Resource
    private StreamBridge streamBridge;
    @Resource
    private StateMachineService stateMachineService;
    @Autowired
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> STOCK_CHECK_SCRIPT = new DefaultRedisScript<>();

    static {
        // Lua脚本：检查并扣减库存
        STOCK_CHECK_SCRIPT.setScriptText(
            "local stockKey = KEYS[1]\n" +
            "local stockLimitStr = ARGV[1]\n" +
            "if not stockLimitStr then\n" +
            "    return 0\n" +
            "end\n" +
            "local stockLimit = tonumber(stockLimitStr)\n" +
            "if not stockLimit then\n" +
            "    return 0\n" +
            "end\n" +
            "local currentStock = tonumber(redis.call('get', stockKey) or '0')\n" +
            "if currentStock >= stockLimit then\n" +
            "    return 0\n" +
            "end\n" +
            "local newStock = redis.call('incr', stockKey)\n" +
            "if newStock <= stockLimit then\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end"
        );
        STOCK_CHECK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void execute(SeckillMockRequestDTO requestDto) {
        taskExecutor.execute(() -> {
            try {
                doExecute(requestDto);
            } catch (Exception e) {
                log.error("秒杀失败", e);
            }
        });
    }

    private void doExecute(SeckillMockRequestDTO requestDto) {
        long seckillId = requestDto.getSeckillId();
        Seckill seckill = redisService.getSeckill(seckillId);

        // 使用Lua脚本检查并扣减库存
        String stockKey = String.valueOf(seckillId);
        String stockLimit = String.valueOf(seckill.getNumber());

        // 使用StringRedisTemplate执行脚本，确保参数正确传递
        Long result = stringRedisTemplate.execute(
            STOCK_CHECK_SCRIPT,
            Collections.singletonList(stockKey),
            stockLimit
        );

        if (result == 1) {
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
            RLock lock = redissonClient.getLock(requestDto.getSeckillId() + ":" + REDIS_MONGO_REACTIVE.getCode());
            lock.lock();
            try {
                seckill = redisService.getSeckill(seckillId);
                if (stateMachineService.checkState(seckillId, States.IN_PROGRESS)) {
                    stateMachineService.feedMachine(Events.ACTIVITY_CALCULATE, seckillId);
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
            } finally {
                lock.unlock();
            }
        }
    }
}
