package com.goodskill.chat.common.http;

import com.goodskill.chat.client.handler.ChatClientHandler;
import com.goodskill.chat.server.handler.ChatServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

/**
 *
 */
public class HttpPipelineInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpPipelineInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            // http消息聚合
            pipeline.addLast("codec", new HttpClientCodec());
            pipeline.addLast("aggregator",
                    new HttpObjectAggregator(512 * 1024));
            // http消息压缩
            pipeline.addLast("decompressor",
                    new HttpContentDecompressor());
            pipeline.addLast(new ChatClientHandler());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
            pipeline.addLast("aggregator",
                    new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(new ChatServerHandler());
            pipeline.addLast("compressor",
                    new HttpContentCompressor());
        }
        System.out.println(pipeline);
    }
}