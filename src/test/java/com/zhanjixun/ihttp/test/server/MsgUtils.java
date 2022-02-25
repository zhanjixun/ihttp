package com.zhanjixun.ihttp.test.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhanjixun
 * @date 2021-04-14 16:48:41
 */
public class MsgUtils {

    public static FullHttpResponse write(int status, String text, String contentType) {
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
    public static FullHttpResponse write(int status, String text, String contentType, Map<String, String> headers) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status), byteBuf);
        response.headers().set("Content-Type", contentType);
        for (Map.Entry<String, String> entry : Optional.ofNullable(headers).orElse(new HashMap<>()).entrySet()) {
            response.headers().set(entry.getKey(), entry.getValue());
        }
        return response;
    }

    public static FullHttpResponse writeHtml(String text) {
        return write(200, text, "text/html; charset=UTF-8");
    }

    public static FullHttpResponse writeText(String text) {
        return write(200, text, "text/plain; charset=UTF-8");
    }

    public static FullHttpResponse writeJson(Object obj) {
        return write(200, JSON.toJSONString(obj), "application/json; charset=UTF-8");
    }

    public static FullHttpResponse writeJson(Object obj, Map<String, String> headers) {
        return write(200, JSON.toJSONString(obj), "application/json; charset=UTF-8", headers);
    }

}
