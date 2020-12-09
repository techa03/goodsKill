package com.goodskill.chat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

public class ChatClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        System.out.println(msg.content().toString(CharsetUtil.UTF_8));
    }
}
