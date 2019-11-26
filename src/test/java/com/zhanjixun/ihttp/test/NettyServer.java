package com.zhanjixun.ihttp.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer extends Thread {

	private int port;

	private EventLoopGroup group = new NioEventLoopGroup();

	private ServerBootstrap serverBootstrap = new ServerBootstrap();

	public NettyServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			serverBootstrap.group(group);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new NettyServerFilter());
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			log.debug("服务端启动成功.端口是:" + port);
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	public void shutdownGracefully() {
		group.shutdownGracefully();
		log.debug("关闭服务器...");
	}

	class NettyServerFilter extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline channelPipeline = ch.pipeline();
			channelPipeline.addLast("encoder", new HttpResponseEncoder());
			channelPipeline.addLast("decoder", new HttpRequestDecoder());
			channelPipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
			channelPipeline.addLast("handler", new NettyServerHandler());
		}
	}
}