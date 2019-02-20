package org.seckill.web.chat;

import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.dto.ChatMessageDto;
import org.seckill.web.cache.ChatMessageCacheUtil;


/**
 * @author heng
 */
@ChannelHandler.Sharable
@Slf4j
public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setMessage("Cilent Netty rocks!");
        ctx.writeAndFlush(chatMessageDto);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        ChatMessageDto msg;
        if (obj instanceof ChatMessageDto) {
            msg = (ChatMessageDto) obj;
        } else {
            log.error("无法处理该数据！");
            throw new RuntimeException();
        }
        if (log.isDebugEnabled()) {
            log.debug("msg is :{}", msg.toString());
        }
        String key = ctx.channel().id().asShortText();
        Cache<String, ChatMessageDto> userCache = ChatMessageCacheUtil.userCache;
        ChatMessageDto chatMessageDto = userCache.getIfPresent(key);
        if (chatMessageDto == null) {
            userCache.put(key, msg);
        } else {
            msg.setMessage(chatMessageDto.getMessage() + msg.getMessage());
            userCache.put(key, msg);
        }
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
