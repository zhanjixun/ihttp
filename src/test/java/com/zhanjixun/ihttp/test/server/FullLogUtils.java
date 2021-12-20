package com.zhanjixun.ihttp.test.server;

import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.util.Map;

/**
 * @author zhanjixun
 * @date 2021-04-14 16:47:12
 */

public class FullLogUtils {

    public static String fullLog(FullHttpRequest request, FullHttpResponse response) {
        return appendFullRequest(request) + StringUtil.NEWLINE + StringUtil.NEWLINE + appendFullResponse(response);
    }

    private static String appendFullResponse(FullHttpResponse response) {
        StringBuilder builder = new StringBuilder();
        appendInitialLine(builder, response);
        appendHeaders(builder, response.headers());
        appendHeaders(builder, response.trailingHeaders());
        builder.append(StringUtil.NEWLINE);
        builder.append(response.content().toString(CharsetUtil.UTF_8));
        return builder.toString();
    }

    public static String appendFullRequest(FullHttpRequest request) {
        StringBuilder builder = new StringBuilder();
        appendInitialLine(builder, request);
        appendHeaders(builder, request.headers());
        appendHeaders(builder, request.trailingHeaders());
        builder.append(StringUtil.NEWLINE);
        builder.append(request.content().toString(CharsetUtil.UTF_8));
        return builder.toString();
    }

    private static void appendInitialLine(StringBuilder builder, HttpRequest req) {
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

    private static void appendHeaders(StringBuilder builder, HttpHeaders headers) {
        for (Map.Entry<String, String> e : headers) {
            builder.append(e.getKey());
            builder.append(": ");
            builder.append(e.getValue());
            builder.append(StringUtil.NEWLINE);
        }
    }

}
