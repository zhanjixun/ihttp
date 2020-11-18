package com.zhanjixun.ihttp.test.base;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class NettyServer extends Thread {

    private final int port;

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

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
            log.debug("netty服务器启动,端口:" + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void shutdownGracefully() {
        group.shutdownGracefully();
        log.debug("netty服务器关闭");
    }

    static class NettyServerFilter extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline channelPipeline = ch.pipeline();
            channelPipeline.addLast("encoder", new HttpResponseEncoder());
            channelPipeline.addLast("decoder", new HttpRequestDecoder());
            channelPipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
            channelPipeline.addLast("handler", new NettyServerHandler());
        }
    }

    @Slf4j
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {

        private final Map<String, Function<FullHttpRequest, FullHttpResponse>> controller = new Controller().getController();

        @Override
        public void channelRead(ChannelHandlerContext handlerContext, Object msg) throws Exception {
            FullHttpRequest request = (FullHttpRequest) msg;
            try {

                Function<FullHttpRequest, FullHttpResponse> requestHandler = controller.get(request.uri().split("\\?")[0]);
                if (requestHandler == null) {
                    FullHttpResponse pageNotFound = write(HttpResponseStatus.NOT_FOUND, "page not found", "text/plain; charset=UTF-8");
                    handlerContext.writeAndFlush(pageNotFound).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                FullHttpResponse response = requestHandler.apply(request);
                //请求 返回 日志
                Arrays.stream(fullLog(request, response).split(StringUtil.NEWLINE)).forEach(log::info);
                handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } finally {
                request.release();
            }
        }

        private String fullLog(FullHttpRequest request, FullHttpResponse response) {
            return appendFullRequest(request) + StringUtil.NEWLINE + StringUtil.NEWLINE + appendFullResponse(response);
        }

        private String appendFullResponse(FullHttpResponse response) {
            StringBuilder builder = new StringBuilder();
            appendInitialLine(builder, response);
            appendHeaders(builder, response.headers());
            appendHeaders(builder, response.trailingHeaders());
            builder.append(StringUtil.NEWLINE);
            builder.append(response.content().toString(CharsetUtil.UTF_8));
            return builder.toString();
        }

        @Override
        public void channelActive(ChannelHandlerContext handlerContext) throws Exception {
            log.debug("连接的客户端地址:" + handlerContext.channel().remoteAddress());
            handlerContext.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
            super.channelActive(handlerContext);
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

        private static void appendInitialLine(StringBuilder buf, HttpResponse res) {
            buf.append(res.protocolVersion());
            buf.append(' ');
            buf.append(res.status());
            buf.append(StringUtil.NEWLINE);
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


    public static FullHttpResponse write(HttpResponseStatus status, String text, String contentType) {
        return write(status, text, contentType, new HashMap<>());
    }

    /**
     * 写返回值
     *
     * @param status      状态码
     * @param text        文本
     * @param contentType 内容类型
     * @param headers     返回头
     * @return
     */
    public static FullHttpResponse write(HttpResponseStatus status, String text, String contentType, Map<String, String> headers) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        for (Map.Entry<String, String> entry : Optional.ofNullable(headers).orElse(new HashMap<>()).entrySet()) {
            response.headers().set(entry.getKey(), entry.getValue());
        }
        return response;
    }

    public static FullHttpResponse writeHtml(String text) {
        return write(HttpResponseStatus.OK, text, "text/html; charset=UTF-8");
    }

    public static FullHttpResponse writeText(String text) {
        return write(HttpResponseStatus.OK, text, "text/plain; charset=UTF-8");
    }

    public static FullHttpResponse writeJson(Object obj) {
        return write(HttpResponseStatus.OK, JSON.toJSONString(obj), "application/json; charset=UTF-8");
    }
}