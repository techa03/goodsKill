package com.goodskill.service;

import com.goodskill.common.core.enums.ActivityEvent;
import com.goodskill.common.core.enums.SeckillActivityStatesEnum;
import jakarta.annotation.Resource;
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
@EnableFeignClients({"com.goodskill.order.api", "com.goodskill.es.api"})
@RestController
public class SeckillApplication {
    @Resource
    private StateMachine<SeckillActivityStatesEnum, ActivityEvent> stateMachine;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeckillApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            stateMachine.sendEvent(ActivityEvent.ACTIVITY_CREATE);
            stateMachine.sendEvent(ActivityEvent.ACTIVITY_START);
        };
    }


}
