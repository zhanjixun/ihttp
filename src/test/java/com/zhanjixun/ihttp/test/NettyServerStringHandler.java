package com.zhanjixun.ihttp.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;


public class NettyServerStringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext handlerContext, String msg) throws Exception {
        // 收到消息直接打印输出
        System.out.println("服务端接受的消息 : " + msg);
        //服务端断开的条件
        if ("quit".equals(msg)) {
            handlerContext.close();
        }
        // 返回客户端消息
        handlerContext.writeAndFlush(new Date() + "\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
        System.out.println("连接的客户端地址:" + handlerContext.channel().remoteAddress());
        handlerContext.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ \n");
        super.channelActive(handlerContext);
    }
}
