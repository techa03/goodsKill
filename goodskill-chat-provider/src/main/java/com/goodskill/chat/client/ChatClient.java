package com.goodskill.chat.client;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.chat.common.http.HttpPipelineInitializer;
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
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author heng
 */
@Slf4j
@Component
public class ChatClient {
    static final String URL = System.getProperty("url", "http://127.0.0.1:8080/");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
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
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpPipelineInitializer(true));

            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            DefaultFullHttpRequest multipartRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    "/upload");
            //通过这个工具构造multipart请求格式
            HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(multipartRequest, true);
            //在报文体中添加一个简单属性
            encoder.addBodyAttribute("key1", "value1");
            //在报文体中添加文件上传属性
//            encoder.addBodyFileUpload("file", new File("/Users/linjingfu/Desktop/today.txt"),
//                    HttpHeaderValues.APPLICATION_OCTET_STREAM.toString(), false);
            //发生请求行，以及请求头内容
            HttpRequest requestToBeSend = encoder.finalizeRequest();

            User user = new User();
            user.setAccount("1as");
            user.setUsername("张三");
            user.setId(1);
            BeanUtil.beanToMap(user);

            String token = JwtUtils.createToken(BeanUtil.beanToMap(user));
            requestToBeSend.headers().set("token", token);
            requestToBeSend.headers().set("Accept-Encoding", "gzip,deflate");
            ch.writeAndFlush(requestToBeSend);
            //发送多个"chunk"，即分段发送多个属性及文件内容
            while (true) {
                HttpContent chunk = encoder.readChunk(ch.alloc());
                if (chunk == null) {
                    break;
                }
                ch.writeAndFlush(chunk);
                if (encoder instanceof LastHttpContent) {
                    break;
                }
            }

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }


}