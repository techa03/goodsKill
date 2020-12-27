package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.api.dto.SeckillMockResponseDto;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.service.common.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

import static org.seckill.api.enums.SeckillSolutionEnum.REDIS_MONGO_REACTIVE;

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
    @Autowired
    private SeckillMapper extSeckillMapper;
    @Autowired
    private Source source;

    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        long seckillId = requestDto.getSeckillId();
        Seckill seckill = redisService.getSeckill(seckillId);
        if (redisTemplate.opsForValue().increment(seckillId) < seckill.getNumber()) {
            taskExecutor.execute(() ->
                    source.output().send(MessageBuilder.withPayload(
                            SeckillMockResponseDto
                                    .builder()
                                    .seckillId(seckillId)
                                    .note(REDIS_MONGO_REACTIVE.getName())
                                    .build())
                            .build())
            );
            log.info("已发送");
        } else {
            synchronized (this) {
                seckill = redisService.getSeckill(seckillId);
                if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                    log.info("秒杀商品暂无库存，发送活动结束消息！");
                    source.output().send(MessageBuilder.withPayload(
                            SeckillMockResponseDto
                                    .builder()
                                    .seckillId(seckillId)
                                    .note(REDIS_MONGO_REACTIVE.getName())
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
