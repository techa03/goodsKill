package com.goodskill.order;

import com.goodskill.order.entity.OrderDTO;
import com.goodskill.order.service.impl.OrderServiceImpl;
import com.goodskill.order.vo.SeckillMockSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author heng
 */
@SpringBootApplication(scanBasePackages = {"com.goodskill.order", "com.goodskill.core"})
@EnableMongoRepositories(basePackages = "com.goodskill.order.repository")
@Slf4j
public class OrderApplication {

    @Autowired
    private OrderServiceImpl mongoService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrderApplication.class)
                .web(WebApplicationType.SERVLET)
                .registerShutdownHook(true)
                .run(args);
    }

    /**
     * 监听seckillMongoSave队列消息
     *
     * @return
     */
    @Bean
    public Consumer<SeckillMockSaveVo> seckillMongoSave() {
        return it -> {
            log.info("接收到消息:{}", it);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setSeckillId(BigInteger.valueOf(it.getSeckillId()));
            orderDTO.setUserPhone(it.getUserPhone());
            orderDTO.setCreateTime(LocalDateTime.now());
            mongoService.saveRecord(orderDTO);
        };
    }

}
