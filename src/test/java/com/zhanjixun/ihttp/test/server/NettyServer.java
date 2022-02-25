package com.zhanjixun.ihttp.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class NettyServer extends Thread {

    private final int port;

    private final EventLoopGroup group = new NioEventLoopGroup();

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private final String basePath;

    public NettyServer(int port, String basePath) {
        this.port = port;
        this.basePath = basePath;
    }

    @Override
    public void run() {
        try {
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    channelPipeline.addLast("encoder", new HttpResponseEncoder());
                    channelPipeline.addLast("decoder", new HttpRequestDecoder());
                    channelPipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
                    channelPipeline.addLast("handler", new NettyServerHandler(basePath));
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.debug("netty server start on port:" + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void shutdownGracefully() {
        group.shutdownGracefully();
        log.debug("netty server shutdown");
    }

    @Slf4j
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * controller 类名-示例 map
         */
        private final Map<String, Object> controller;
        /**
         * url-方法 map
         */
        private final Map<String, Method> dispatcherMap;

        public NettyServerHandler(String basePath) {
            List<Class<?>> list = PackageUtils.listType(basePath).stream()
                    .filter(t -> Arrays.stream(t.getDeclaredMethods()).anyMatch(m -> m.isAnnotationPresent(RequestMapping.class)))
                    .collect(Collectors.toList());
            controller = list.stream().collect(Collectors.toMap(Class::getName, d -> {
                try {
                    return d.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }));
            dispatcherMap = list.stream().flatMap(t -> Arrays.stream(t.getDeclaredMethods())).filter(m -> m.isAnnotationPresent(RequestMapping.class))
                    .collect(Collectors.toMap(m -> Optional.ofNullable(m.getDeclaringClass().getAnnotation(RequestMapping.class)).map(RequestMapping::value).orElse("") +
                            m.getAnnotation(RequestMapping.class).value(), m -> m));
        }

        @Override
        public void channelRead(ChannelHandlerContext handlerContext, Object msg) {
            FullHttpRequest request = (FullHttpRequest) msg;
            try {
                Method requestHandler = dispatcherMap.get(request.uri().split("\\?")[0]);
                //url找不到
                if (requestHandler == null) {
                    FullHttpResponse pageNotFound = MsgUtils.write(404, "page not found", "text/plain; charset=UTF-8");
                    handlerContext.writeAndFlush(pageNotFound).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                //请求方法不匹配
                String[] method = requestHandler.getAnnotation(RequestMapping.class).method();
                String httpRequestMethod = request.method().name();
                if (Arrays.stream(method).noneMatch(d -> d.equalsIgnoreCase(httpRequestMethod))) {
                    FullHttpResponse pageNotFound = MsgUtils.write(404, "page not found", "text/plain; charset=UTF-8");
                    handlerContext.writeAndFlush(pageNotFound).addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                //调用Controller接口方法
                Object obj = controller.get(requestHandler.getDeclaringClass().getName());
                FullHttpResponse response = (FullHttpResponse) requestHandler.invoke(obj, request);
                //请求 返回 日志
                Arrays.stream(FullLogUtils.fullLog(request, response).split(StringUtil.NEWLINE)).forEach(log::info);
                handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
                e.printStackTrace();
                FullHttpResponse serverError = MsgUtils.write(500, "server error: " + e.getMessage(), "text/plain; charset=UTF-8");
                handlerContext.writeAndFlush(serverError).addListener(ChannelFutureListener.CLOSE);
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
    }

}