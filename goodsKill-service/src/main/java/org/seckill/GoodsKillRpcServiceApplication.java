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
    private static Logger _log = LoggerFactory.getLogger(GoodsKillRpcServiceApplication.class);

    public static void main(String[] args) throws IOException {
        _log.info(">>>>> goodsKill-rpc-service 正在启动 <<<<<");
        ClassPathXmlApplicationContext context= new ClassPathXmlApplicationContext(
                new String[]{"classpath:META-INF/spring/spring-*.xml", "classpath*:junit/spring/spring-dao.xml"});
        context.start();
        System.in.read();
        _log.info(">>>>> goodsKill-rpc-service 启动完成 <<<<<");
    }

}