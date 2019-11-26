package com.zhanjixun.ihttp.test;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public class NettyServerStringHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext, String msg) {
		System.out.println("服务端接受的消息 : " + msg);

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
		log.debug("连接的客户端地址:" + handlerContext.channel().remoteAddress());
		handlerContext.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ \n");
		super.channelActive(handlerContext);
	}
}
