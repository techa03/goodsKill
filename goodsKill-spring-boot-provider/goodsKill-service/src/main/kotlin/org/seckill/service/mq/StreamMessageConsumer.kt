package org.seckill.service.mq

import org.seckill.api.dto.SeckillMockRequestDto
import org.seckill.api.enums.SeckillSolutionEnum
import org.seckill.service.inner.SeckillExecutor
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import javax.annotation.Resource

/**
 * spring cloud stream消息消费者
 *
 * @author techa03
 * @date 2020/7/18
 */
@EnableBinding(value = [Sink::class, Source::class])
open class StreamMessageConsumer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Resource
    private lateinit var seckillExecutor: SeckillExecutor

    @StreamListener(Sink.INPUT)
    fun consumer(payload: SeckillMockRequestDto) {
        log.info("收到秒杀请求:$payload")
        val userPhone = payload.phoneNumber
        val seckillId = payload.seckillId
        seckillExecutor.dealSeckill(seckillId, userPhone, SeckillSolutionEnum.RABBIT_MQ.name)
    }
}