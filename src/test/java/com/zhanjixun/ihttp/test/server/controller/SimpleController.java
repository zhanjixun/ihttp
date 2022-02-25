package com.zhanjixun.ihttp.test.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.test.server.MsgUtils;
import com.zhanjixun.ihttp.test.server.RequestMapping;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.multipart.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
        return MsgUtils.writeJson(JSON.toJSONString(map), ImmutableMap.of("Set-Cookie", "token=" + System.currentTimeMillis()));
    }

    @RequestMapping(value = "/uploadFile", method = "POST")
    public FullHttpResponse postFile(FullHttpRequest request) throws IOException {
        Map<String, FileUpload> fileUploadMap = decodeFile(request);
        Map<String, String> map = decodeBody(request);
        String content = new String(fileUploadMap.get("file1").get());
        return MsgUtils.writeJson(ImmutableMap.of("status", "ok", "content", content));
    }

    @RequestMapping(value = "/getListInfo", method = "GET")
    public FullHttpResponse getListInfo(FullHttpRequest request) {
        return MsgUtils.writeJson(ImmutableMap.of("data", Lists.newArrayList(
                ImmutableMap.of("id", "1", "name", "jack"),
                ImmutableMap.of("id", "2", "name", "rose"),
                ImmutableMap.of("id", "3", "name", "rookie"))));
    }

    private Map<String, FileUpload> decodeFile(FullHttpRequest request) {
        HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(true), request);
        httpDecoder.setDiscardThreshold(0);
        httpDecoder.offer(request);
        Map<String, FileUpload> map = new HashMap<>();
        for (InterfaceHttpData bodyHttpData : httpDecoder.getBodyHttpDatas()) {
            if (bodyHttpData == null) {
                continue;
            }
            if (InterfaceHttpData.HttpDataType.FileUpload.equals(bodyHttpData.getHttpDataType())) {
                FileUpload fileUpload = (FileUpload) bodyHttpData;
                map.put(fileUpload.getName(), fileUpload);
            }
        }
        return map;
    }

    private Map<String, String> decodeBody(FullHttpRequest request) {
        HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(true), request);
        httpDecoder.setDiscardThreshold(0);
        httpDecoder.offer(request);
        Map<String, String> map = new HashMap<>();
        for (InterfaceHttpData bodyHttpData : httpDecoder.getBodyHttpDatas()) {
            if (bodyHttpData == null) {
                continue;
            }
            if (InterfaceHttpData.HttpDataType.Attribute.equals(bodyHttpData.getHttpDataType())) {
                try {
                    Attribute attribute = (Attribute) bodyHttpData;
                    map.put(attribute.getName(), attribute.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
