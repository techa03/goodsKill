package com.goodskill.chat.server;

import com.goodskill.chat.common.http.HttpPipelineInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author heng
 */
@Slf4j
@Component
public class ChatServer implements InitializingBean {
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new HttpPipelineInitializer(false));
            ChannelFuture f = b.bind().sync();
            log.info("{} started and listen on {}", ChatServer.class.getName(), f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    @Override
    public void afterPropertiesSet() {
        taskExecutor.execute(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                log.error("聊天服务中断！", e);
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
//        User user = new User();
//        user.setAccount("1as");
//        user.setUsername("张三");
//        user.setId(1);
//        BeanUtil.beanToMap(user);
//
//        String token = JwtUtils.createToken(BeanUtil.beanToMap(user));
//        System.out.println(token);
//        JwtUtils.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNzNhYmIwZS00MGFkLTQxYWItYWEyMC0wNjFhZjI0YWRjOWQiLCJpYXQiOjE2MDc0MTEyODQsImV4cCI6MTYwNzQxNDg4NCwibmFtZSI6ImRmZHNmIiwidXNlciI6IuW8oOS4iSJ9.E1S3apj9SZwCVffQ8eji0qSXh0Cv3fSzIZA6h4VS9do");
        new ChatServer(8080).start();
    }
}
