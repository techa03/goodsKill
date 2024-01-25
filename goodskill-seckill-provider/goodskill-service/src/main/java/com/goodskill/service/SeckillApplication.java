package com.goodskill.service;

import com.goodskill.common.core.enums.ActivityEvent;
import com.goodskill.common.core.enums.SeckillActivityStates;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 *
 * @author techa
 */
@SpringBootApplication(scanBasePackages = {
        "com.goodskill.service.**",
        "com.goodskill.common.core",
})
@EnableTransactionManagement
@MapperScan("com.goodskill.service.mapper")
@EnableDiscoveryClient
@EnableFeignClients({"com.goodskill.order.api"})
@RestController
@Slf4j
public class SeckillApplication {
    @Resource
    private StateMachine<SeckillActivityStates, ActivityEvent> stateMachine;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeckillApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
//            feedMachine(stateMachine, ActivityEvent.ACTIVITY_CREATE);
//            feedMachine(stateMachine, ActivityEvent.ACTIVITY_START);
//            feedMachine(stateMachine, ActivityEvent.ACTIVITY_END);
//            boolean b = feedMachine(stateMachine, ActivityEvent.ACTIVITY_RESET);
            log.info("feedMachine");
        };
    }




}
