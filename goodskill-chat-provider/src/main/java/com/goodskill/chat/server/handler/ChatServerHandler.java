package com.goodskill.chat.server.handler;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.chat.dto.ChatMessageDto;
import com.goodskill.common.JwtUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.seckill.entity.User;

import java.io.IOException;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author heng
 */
@Slf4j
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws InterruptedException {
        ChatMessageDto msg = new ChatMessageDto();
        User user;
        if (obj instanceof FullHttpRequest) {
            String token = ((FullHttpRequest) obj).headers().get("token");
            String message = ((FullHttpRequest) obj).headers().get("msg");
            new HttpRequestDecoder();
            log.info("token: {}", token);
            Map map = JwtUtils.parseToken(token);
            user = BeanUtil.mapToBean(map, User.class, false, null);

            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder((HttpRequest) obj);
            while (decoder.hasNext()) {
                InterfaceHttpData httpData = decoder.next();
                if (httpData instanceof Attribute) {
                    Attribute attr = (Attribute) httpData;
                    log.info("收到mutlipart属性：" + attr);
                    try {
                        msg.setMessage(attr.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (httpData instanceof FileUpload) {
                    FileUpload fileUpload = (FileUpload) httpData;
                    log.info("收到multipart文件：" + fileUpload);
                    // TODO 处理文件上传
                }
            }
            decoder.destroy();
        } else {
            log.error("无法处理该数据！");
            throw new RuntimeException();
        }
        String account;
        if (user == null) {
            account = ctx.channel().remoteAddress().toString();
        } else {
            account = user.getAccount();
        }
        String inMessage = msg.getMessage();
        Channel inComing = ctx.channel();
        String outMessage;
        for (Channel channel : channels) {
            if (channel != inComing) {
                outMessage = "[用户" + account + " 说]" + inMessage + "\n";
            } else {
                outMessage = "[我说]" + inMessage + "\n";
            }
            System.out.println(outMessage);
            msg.setMessage(outMessage);
            FullHttpResponse response = getFullHttpResponse((HttpMessage) obj, outMessage);
            channel.writeAndFlush(response);
        }
    }

    @NotNull
    private FullHttpResponse getFullHttpResponse(HttpMessage obj, String outMessage) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(obj);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(outMessage, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        return response;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.writeAndFlush(Unpooled.copiedBuffer("ddfd", CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel inComing = ctx.channel();
        //通知其他客户端有新人进入
        for (Channel channel : channels) {
            if (channel != inComing) {
                String msg = "[欢迎: " + inComing.remoteAddress() + "] 进入聊天室！\n";
                System.out.println(msg);
                ChatMessageDto chatMessageDto = new ChatMessageDto();
                chatMessageDto.setMessage(msg);
                channel.writeAndFlush(chatMessageDto);
            }
        }
        channels.add(inComing);
    }

}
