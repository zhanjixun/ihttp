package com.zhanjixun.ihttp.test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        //处理http服务的关键handler
        channelPipeline.addLast("encoder", new HttpResponseEncoder());
        channelPipeline.addLast("decoder", new HttpRequestDecoder());
        channelPipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        channelPipeline.addLast("handler", new NettyServerHandler());// 服务端业务逻辑
    }
}