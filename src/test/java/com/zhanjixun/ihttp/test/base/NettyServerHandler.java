package com.zhanjixun.ihttp.test.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Function<FullHttpRequest, FullHttpResponse>> controller = new HashMap<>();

    public NettyServerHandler() {
        controller.putIfAbsent("/index", (request) -> text("ok"));
        controller.putIfAbsent("/upload", (request) -> text("ok"));
    }

    @Override
    public void channelRead(ChannelHandlerContext handlerContext, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        try {
            System.out.println(appendFullRequest(request));
            Function<FullHttpRequest, FullHttpResponse> requestHandler = controller.get(request.uri().split("\\?")[0]);
            if (requestHandler == null) {
                ByteBuf byteBuf = Unpooled.copiedBuffer("page not found", CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, byteBuf);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }
            FullHttpResponse response = requestHandler.apply(request);
            handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } finally {
            request.release();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
        log.debug("连接的客户端地址:" + handlerContext.channel().remoteAddress());
        handlerContext.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(handlerContext);
    }

    private FullHttpResponse text(String text) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }

    public String appendFullRequest(FullHttpRequest request) {
        StringBuilder builder = new StringBuilder();
        appendInitialLine(builder, request);
        appendHeaders(builder, request.headers());
        appendHeaders(builder, request.trailingHeaders());
        builder.append(StringUtil.NEWLINE);
        builder.append(request.content().toString(CharsetUtil.UTF_8));
        return builder.toString();
    }

    private void appendInitialLine(StringBuilder builder, HttpRequest req) {
        builder.append(req.method());
        builder.append(' ');
        builder.append(req.uri());
        builder.append(' ');
        builder.append(req.protocolVersion());
        builder.append(StringUtil.NEWLINE);
    }

    private void appendHeaders(StringBuilder builder, HttpHeaders headers) {
        for (Map.Entry<String, String> e : headers) {
            builder.append(e.getKey());
            builder.append(": ");
            builder.append(e.getValue());
            builder.append(StringUtil.NEWLINE);
        }
    }

}