package com.zhanjixun.ihttp.test.base;

import com.google.common.collect.ImmutableMap;
import com.zhanjixun.ihttp.test.Student;
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
        controller.putIfAbsent("/index", (request) -> NettyServer.writeText("ok"));
        controller.putIfAbsent("/home", (request) -> NettyServer.writeHtml("<!DOCTYPE HTML><html><body>hello</body></html>"));
        controller.putIfAbsent("/upload", (request) -> NettyServer.writeJson(ImmutableMap.of("status", 200, "msg", "ok")));
        controller.putIfAbsent("/student", (request) -> NettyServer.writeJson(new Student("中文测试", 23, "JK1132")));
    }

}
