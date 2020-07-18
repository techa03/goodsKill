//package org.seckill.service.mq;
//
//import lombok.extern.slf4j.Slf4j;
//import org.seckill.api.dto.SeckillMockRequestDto;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.StreamListener;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * spring cloud stream消息消费者
// *
// * @author techa03
// * @date 2020/7/18
// */
//@EnableBinding(value = {
//        SeckillTopic.class
//})
//@Slf4j
//public class StreamMessageConsumer {
//
//    @StreamListener(SeckillTopic.INPUT)
//    public void consumer(SeckillMockRequestDto payload) {
//        log.info("收到pauload:{}", payload);
//    }
//
//}
