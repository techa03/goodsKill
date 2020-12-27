package com.goodskill.mongo.topic;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 秒杀请求保存通知消息
 *
 */
public interface SeckillMockSaveTopic {
    String OUTPUT = "seckill-mongo-save-produce";

    String INPUT = "seckill-mongo-save-consume";

    /**
     * @return output channel
     */
    @Output(OUTPUT)
    MessageChannel output();

    @Input(INPUT)
    SubscribableChannel input();
}
