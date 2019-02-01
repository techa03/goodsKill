package org.seckill.web.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;


/**
 * @author heng
 */
@ChannelHandler.Sharable
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setMessage("Cilent Netty rocks!");
        ctx.writeAndFlush(chatMessageDto);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.print("Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        ctx.channel().writeAndFlush(evt);
    }

}
