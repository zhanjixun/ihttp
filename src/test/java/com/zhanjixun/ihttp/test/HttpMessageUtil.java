package com.zhanjixun.ihttp.test;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * @author :zhanjixun
 * @date : 2019/11/26 15:06
 * @contact :zhanjixun@qq.com
 */
@UtilityClass
public class HttpMessageUtil {

	public static StringBuilder appendFullRequest(StringBuilder builder, FullHttpRequest request) {
		appendInitialLine(builder, request);
		appendHeaders(builder, request.headers());
		appendHeaders(builder, request.trailingHeaders());
		builder.append(StringUtil.NEWLINE);
		builder.append(request.content().toString(CharsetUtil.UTF_8));
		return builder;
	}


	private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
		buf.append(req.method());
		buf.append(' ');
		buf.append(req.uri());
		buf.append(' ');
		buf.append(req.protocolVersion());
		buf.append(StringUtil.NEWLINE);
	}


	private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
		for (Map.Entry<String, String> e : headers) {
			buf.append(e.getKey());
			buf.append(": ");
			buf.append(e.getValue());
			buf.append(StringUtil.NEWLINE);
		}
	}


}
