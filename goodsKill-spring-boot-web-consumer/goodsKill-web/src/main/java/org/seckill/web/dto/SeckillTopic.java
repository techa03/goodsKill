package org.seckill.web.dto;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author techa03
 * @date 2020/7/16
 */
public interface SeckillTopic {
    String INPUT = "seckill-consumer";
    String OUTPUT = "seckill-producer";

    @Input(INPUT)
    SubscribableChannel input();

    @Output(OUTPUT)
    MessageChannel output();
}
