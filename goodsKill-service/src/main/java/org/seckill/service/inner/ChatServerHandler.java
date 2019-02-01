package org.seckill.service.inner;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;

/**
 * @author heng
 */
@Slf4j
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        ChatMessageDto msg = null;
        if (obj instanceof ChatMessageDto) {
            msg = (ChatMessageDto) obj;
        } else {
            log.error("无法处理该数据！");
            throw new RuntimeException();
        }
        Channel inComing = ctx.channel();

        for (Channel channel : channels) {
            if (channel != inComing) {
                String msg1 = "[用户" + msg.getUser().getAccount() + " 说：]" + msg.getMessage() + "\n";
                channel.writeAndFlush(Unpooled.copiedBuffer(msg1, CharsetUtil.UTF_8));
            } else {
                String msg1 = "[我说：]" + msg.getMessage() + "\n";
                channel.writeAndFlush(Unpooled.copiedBuffer(msg1, CharsetUtil.UTF_8));
            }
        }
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
                channel.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            }
        }
        channels.add(inComing);
    }

}
