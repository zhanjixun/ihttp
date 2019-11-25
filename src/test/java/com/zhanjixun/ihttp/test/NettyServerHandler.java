package com.zhanjixun.ihttp.test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext handlerContext, Object msg) throws Exception {
        String result = "ok";
        if (!(msg instanceof FullHttpRequest)) {
            result = "未知请求!";
            send(handlerContext, result, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        FullHttpRequest request = (FullHttpRequest) msg;
        try {
            String path = request.uri();          //获取路径
            String body = request.content().toString(CharsetUtil.UTF_8);     //获取参数
            HttpMethod method = request.method();//获取请求方法
            HttpHeaders headers = request.headers();

            send(handlerContext, result, HttpResponseStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放请求
            request.release();
        }
    }

    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
        System.out.println("连接的客户端地址:" + handlerContext.channel().remoteAddress());
        handlerContext.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(handlerContext);
    }
}