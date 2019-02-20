package org.seckill.service.inner;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
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
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws InterruptedException {
        ChatMessageDto msg = null;
        if (obj instanceof ChatMessageDto) {
            msg = (ChatMessageDto) obj;
        } else {
            log.error("无法处理该数据！");
            throw new RuntimeException();
        }
        String account = null;
        if(msg.getUser() == null){
            account = ctx.channel().remoteAddress().toString();
        }else{
            account = msg.getUser().getAccount();
        }
        String inMessage = msg.getMessage();
        Channel inComing = ctx.channel();
        String outMessage = null;
        for (Channel channel : channels) {
            if (channel != inComing) {
                outMessage = "[用户" + account + " 说]" + inMessage + "\n";
                msg.setUser(null);
                msg.setMessage(outMessage);
                channel.writeAndFlush(msg);
                // 经测试需要在此睡眠，否则收发消息会出现异常，实际情况为：所有客户端会同时收到同一种消息
                Thread.sleep(10);
            } else {
                outMessage = "[我说]" + inMessage + "\n";
                msg.setUser(null);
                msg.setMessage(outMessage);
                channel.writeAndFlush(msg);
                // 经测试需要在此睡眠，否则收发消息会出现异常，实际情况为：所有客户端会同时收到同一种消息
                Thread.sleep(10);
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
                ChatMessageDto chatMessageDto = new ChatMessageDto();
                chatMessageDto.setMessage(msg);
                channel.writeAndFlush(chatMessageDto);
            }
        }
        channels.add(inComing);
    }

}
