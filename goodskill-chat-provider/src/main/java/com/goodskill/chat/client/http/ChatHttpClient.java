package com.goodskill.chat.client.http;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.chat.client.ChatClient;
import com.goodskill.chat.common.http.HttpPipelineInitializer;
import com.goodskill.chat.dto.ChatMessageDto;
import com.goodskill.common.JwtUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import lombok.extern.slf4j.Slf4j;
import org.seckill.entity.User;

import java.net.URI;
import java.net.URISyntaxException;


@Slf4j
public class ChatHttpClient extends ChatClient {
    private static final String URL = System.getProperty("url", "http://127.0.0.1:8080/");

    private Channel channel;

    private EventLoopGroup group;

    @Override
    protected void sendMsg(ChatMessageDto dto) {
        if (channel == null) {
            try {
                channel = initConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Configure the client.
        try {
            DefaultFullHttpRequest multipartRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    "/chat-msg");
            //通过这个工具构造multipart请求格式
            HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(multipartRequest, true);
            //在报文体中添加一个简单属性
            encoder.addBodyAttribute("msg", dto.getMessage());
            //在报文体中添加文件上传属性
//            encoder.addBodyFileUpload("file", new File("/Users/1/Desktop/today.txt"),
//                    HttpHeaderValues.APPLICATION_OCTET_STREAM.toString(), false);
            //发送请求行，以及请求头内容
            HttpRequest requestToBeSend = encoder.finalizeRequest();
            requestToBeSend.headers().set("token", dto.getToken());
            requestToBeSend.headers().set("Accept-Encoding", "gzip,deflate");
            channel.writeAndFlush(requestToBeSend);
            //发送多个"chunk"，即分段发送多个属性及文件内容
            while (true) {
                HttpContent chunk = encoder.readChunk(channel.alloc());
                if (chunk == null) {
                    break;
                }
                channel.writeAndFlush(chunk);
            }
            // Wait for the server to close the connection.
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

    private Channel initConnection() throws InterruptedException {
        URI uri = null;
        try {
            uri = new URI(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            log.error("Only HTTP(S) is supported.");
            return null;
        }
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new HttpPipelineInitializer(true));

        // Make the connection attempt.
        return b.connect(host, port).sync().channel();
    }

    public static void main(String[] args) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setMessage("你好啊！！");

        User user = new User();
        user.setAccount("1as");
        user.setUsername("李四");
        user.setId(1);
        BeanUtil.beanToMap(user);
        String token = JwtUtils.createToken(BeanUtil.beanToMap(user));
        dto.setToken(token);
        new ChatHttpClient().sendMsg(dto);
    }
}
