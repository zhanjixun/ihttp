package com.zhanjixun.ihttp.test.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.test.server.MsgUtils;
import com.zhanjixun.ihttp.test.server.RequestMapping;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试接口放在这里
 *
 * @author zhanjixun
 * @date 2020-11-18 17:09:04
 */
public class SimpleController {

    @RequestMapping("/echo")
    public FullHttpResponse echo(FullHttpRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("method", request.method().name());
        String[] split = request.uri().split("\\?");
        if (split.length == 2) {
            map.put("query", Arrays.stream(split[1].split("&")).collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1])));
        }
        map.put("headers", request.headers().entries().stream().collect(Collectors.toMap(d -> d.getKey(), d -> d.getValue())));
        return MsgUtils.write(200, JSON.toJSONString(map), "application/json; charset=UTF-8", ImmutableMap.of("Set-Cookie", "token=" + System.currentTimeMillis()));
    }

    @RequestMapping("/postFile")
    public FullHttpResponse postFile(FullHttpRequest request) {
        return MsgUtils.writeJson(ImmutableMap.of("status", "ok"));
    }

    @RequestMapping("/getListInfo")
    public FullHttpResponse getListInfo(FullHttpRequest request) {
        List<Map<String, String>> list = Lists.newArrayList(
                ImmutableMap.of("id", "1"),
                ImmutableMap.of("id", "2"),
                ImmutableMap.of("id", "3"));
        return MsgUtils.writeJson(ImmutableMap.of("data", list));
    }
}
