package org.seckill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 服务启动类
 * Created by techa03 on 2017/2/3.
 */
public class GoodsKillRpcServiceApplication {
    private static Logger logger = LoggerFactory.getLogger(GoodsKillRpcServiceApplication.class);

    public static void main(String[] args) throws IOException {
        logger.info(">>>>> goodsKill-rpc-service 正在启动 <<<<<");
        AbstractApplicationContext context= new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/spring-*.xml");
        //程序退出前优雅关闭JVM
        context.registerShutdownHook();
        context.start();
        logger.info(">>>>> goodsKill-rpc-service 启动完成 <<<<<");
    }

}