package com.goodskill.chat.common.http;

import com.goodskill.chat.client.handler.ChatClientHandler;
import com.goodskill.chat.server.handler.ChatServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

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
            // http消息解压
//            pipeline.addLast("decompressor",
//                    new HttpContentDecompressor());
            pipeline.addLast("codec", new HttpClientCodec());
            // http消息聚合
            pipeline.addLast("aggregator",
                    new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(new ChatClientHandler());
        } else {
//            pipeline.addLast("compressor",
//                    new HttpContentCompressor());
            pipeline.addLast("codec", new HttpServerCodec());
            pipeline.addLast("aggregator",
                    new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(new ChatServerHandler());
        }
        System.out.println(pipeline);
    }
}