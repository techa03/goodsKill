package org.seckill.web.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;
import org.seckill.entity.User;
import org.seckill.web.cache.ChatMessageCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Scanner;
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
    @Autowired
    private ChatMessageCacheUtil chatMessageCacheUtil;

    private ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private ChannelFuture start() throws Exception {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)), new ChatClientHandler(), new ObjectEncoder());
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

    public String getChatMessageList(User user) {
        ChatMessageDto chatMessageDto = ChatMessageCacheUtil.userCache.getIfPresent(getInstance(user).channelFuture.channel().id().asShortText());
        if (chatMessageDto != null) {
            return chatMessageDto.getMessage();
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1",8080);
        User user1 = new User();
        user1.setUsername("heng");
        user1.setAccount("heng");
        ChatClient c1 = chatClient.getInstance(user1);
        ChatMessageDto chatMsg = new ChatMessageDto();
        chatMsg.setUser(user1);


        while (true) {
            Scanner scanner = new Scanner(System.in);
            int a = scanner.nextInt();
            chatMsg.setMessage(String.valueOf(a));
            c1.sendMessage(chatMsg);
        }
    }
}