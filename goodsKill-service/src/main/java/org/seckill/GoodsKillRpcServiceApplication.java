package org.seckill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 服务启动类
 * Created by ZhangShuzheng on 2017/2/3.
 */
public class GoodsKillRpcServiceApplication {
    private static Logger logger = LoggerFactory.getLogger(GoodsKillRpcServiceApplication.class);

    public static void main(String[] args) throws IOException {
        logger.info(">>>>> goodsKill-rpc-service 正在启动 <<<<<");
        ClassPathXmlApplicationContext context= new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/spring-*.xml");
        context.start();
        System.in.read();
        logger.info(">>>>> goodsKill-rpc-service 启动完成 <<<<<");
    }

}