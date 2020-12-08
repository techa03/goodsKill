package com.goodskill.chat.client;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.chat.common.http.HttpPipelineInitializer;
import com.goodskill.chat.dto.ChatMessageDto;
import com.goodskill.common.JwtUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.seckill.entity.User;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
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
                .handler(new HttpPipelineInitializer(true));
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

    public void sendMessage(Object obj) {
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


    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("http://127.0.0.1:8080/");
        ChatClient chatClient = new ChatClient("127.0.0.1",8080);
        User user1 = new User();
        user1.setUsername("heng");
        user1.setAccount("heng");
        ChatClient c1 = chatClient.getInstance(user1);
        ChatMessageDto chatMsg = new ChatMessageDto();
        chatMsg.setUser(user1);
        String token = JwtUtils.createToken(BeanUtil.beanToMap(user1));

        while (true) {
            Scanner scanner = new Scanner(System.in);
            int a = scanner.nextInt();
            chatMsg.setMessage(String.valueOf(a));


            // Prepare the HTTP request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath(), Unpooled.EMPTY_BUFFER);
//            request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            request.headers().set("token", token);

            // Send the HTTP request.


            c1.sendMessage(request);
        }
    }
}