package org.seckill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 */
@Slf4j
public class GoodsKillRpcServiceApplication {

    public static void main(String[] args) {
        log.info(">>>>> goodsKill-rpc-service 正在启动 <<<<<");
        AbstractApplicationContext context= new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/spring-*.xml");
        // 程序退出前优雅关闭JVM
        context.registerShutdownHook();
        context.start();
        log.info(">>>>> goodsKill-rpc-service 启动完成 <<<<<");
    }

}