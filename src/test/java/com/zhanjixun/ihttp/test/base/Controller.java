package com.zhanjixun.ihttp.test.base;

import com.google.common.collect.ImmutableMap;
import com.zhanjixun.ihttp.test.TestMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 测试接口放在这里
 *
 * @author zhanjixun
 * @date 2020-11-18 17:09:04
 */
public class Controller {

    @Getter
    private final Map<String, Function<FullHttpRequest, FullHttpResponse>> controller = new HashMap<>();

    public Controller() {
        controller.putIfAbsent("/index", (request) -> NettyServer.writeHtml("<!DOCTYPE HTML><html><body>hello</body></html>"));
        controller.putIfAbsent("/postFile", (request) -> NettyServer.writeJson(ImmutableMap.of("status", "ok")));
        controller.putIfAbsent("/home", (request) -> NettyServer.writeHtml("<!DOCTYPE HTML><html><body><div id='val' class='tips' data-val='3284'>hello1</div><div class='tips' data-val='3284'>hello2</div></body></html>"));
        controller.putIfAbsent("/update", (request) -> NettyServer.writeJson(ImmutableMap.of("status", 200, "msg", "ok")));
        controller.putIfAbsent("/upload", (request) -> NettyServer.writeJson(ImmutableMap.of("status", 200, "msg", "ok")));
        controller.putIfAbsent("/student", (request) -> NettyServer.writeJson(new TestMapper.Student("中文测试", 23, "JK1132")));
        controller.putIfAbsent("/student2", (request) -> NettyServer.writeJson(ImmutableMap.of("data", new TestMapper.Student("中文测试22", 23, "JK1132"))));
    }

}
