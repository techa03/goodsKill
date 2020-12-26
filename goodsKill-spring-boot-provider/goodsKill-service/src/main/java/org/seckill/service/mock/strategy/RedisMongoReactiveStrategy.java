package org.seckill.service.mock.strategy;

import lombok.extern.slf4j.Slf4j;
import org.seckill.api.constant.SeckillStatusConstant;
import org.seckill.api.dto.SeckillMockRequestDto;
import org.seckill.entity.Seckill;
import org.seckill.mp.dao.mapper.SeckillMapper;
import org.seckill.service.common.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Override
    public void execute(SeckillMockRequestDto requestDto) {
        long seckillId = requestDto.getSeckillId();
        Seckill seckill = redisService.getSeckill(seckillId);
        if (redisTemplate.opsForValue().increment(seckillId) < seckill.getNumber()) {
            taskExecutor.execute(() ->
                            // FIXME: 2020/12/26 去除activemq，使用其他mq替代
//                    jmsTemplate.send("SUCCESS_KILLED_RESULT", session -> {
//                        Message message = session.createMessage();
//                        message.setLongProperty("seckillId", seckillId);
//                        message.setStringProperty("userPhone", String.valueOf(1));
//                        message.setStringProperty("note", REDIS_MONGO_REACTIVE.getName());
//                        return message;
//                    })
                            System.out.println()
            );
            log.info("已发送");
        } else {
            synchronized (this) {
                seckill = redisService.getSeckill(seckillId);
                if (!SeckillStatusConstant.END.equals(seckill.getStatus())) {
                    log.info("秒杀商品暂无库存，发送活动结束消息！");
                    // FIXME: 2020/12/26 去除activemq，使用其他mq替代
//                    activeMqMessageSender.sendSeckillSuccessTopic(seckillId, REDIS_MONGO_REACTIVE.getName());
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
