package org.seckill.web.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;
import org.seckill.entity.User;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author heng
 */
@Slf4j
@Component
public class ChatClient {
    private Map<User, ChatClient> chatClientMap = new ConcurrentHashMap<>();
    private String host;
    private int port;
    private EventLoopGroup eventLoopGroup;
    private ChannelFuture channelFuture;

    private ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ChannelFuture start() throws Exception {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ChatClientHandler(), new ObjectEncoder());
                    }
                });
        ChannelFuture f = b.connect().sync();
        channelFuture = f;
        return f;
    }

    public void shutdown() {
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        eventLoopGroup.shutdownGracefully();
    }

    public void sendMessage(ChatMessageDto obj) {
        channelFuture.channel().writeAndFlush(obj);
    }

    public ChatClient getInstance(User user) {
        ChatClient chatClient = chatClientMap.get(user);
        if (chatClient != null) {
            return chatClient;
        } else {
            ChatClient client = new ChatClient(host, port);
            try {
                client.start();
            } catch (Exception e) {
                log.error("聊天客户端连接失败！", e);
            }
            chatClientMap.put(user, client);
            return client;
        }
    }

}