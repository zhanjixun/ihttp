package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.RandomGenerator;
import com.zhanjixun.ihttp.parsing.Retryable;
import com.zhanjixun.ihttp.parsing.TimestampGenerator;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 对应一个Mapper中定义的方法
 *
 * @author zhanjixun
 */
@Data
public class MapperMethod {

	private Mapper mapper;

	private String name;

	//注解属性

	private String url;

	private String requestCharset;

	private String responseCharset;

	private Boolean followRedirects;

	//GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
	private String requestMethod;

	private Map<String, String> requestParams;

	private Map<String, String> requestHeaders;

	private Map<String, String> requestMultiParts;

	private int[] assertStatusCode;

	private Boolean disableCookie;

	private List<RandomGenerator> randomGeneratorParams;

	private List<RandomGenerator> randomGeneratorPlaceholders;

	private List<TimestampGenerator> timestampGeneratorParams;

	private List<TimestampGenerator> timestampGeneratorPlaceholders;

	private String requestBody;

	private Retryable retryable;

	private MapperParameter[] parameters;

	public MapperMethod(String name) {
		this.name = name;
	}

	public Response execute(Object... args) throws Exception {
		Request request = buildRequest(args);
		return mapper.getExecutor().execute(request);
	}

	private Request buildRequest(Object... args) {
		Request request = new Request();
		request.setName(name);
		//1.绑定固定内容
		request.setUrl(buildUrl(mapper.getUrl(), getUrl()));
		request.setMethod(getRequestMethod());

		request.setCharset(getRequestCharset());
		request.setResponseCharset(getResponseCharset());

		request.setFollowRedirects(getFollowRedirects());
		request.setBody(getRequestBody());

		request.setHeaders(new HashMap<>());
		Util.ifNotNull(mapper.getRequestHeaders(), headers -> request.getHeaders().putAll(headers));
		Util.ifNotNull(getRequestHeaders(), headers -> request.getHeaders().putAll(headers));

		request.setParams(new HashMap<>());
		Util.ifNotNull(mapper.getRequestParams(), params -> request.getParams().putAll(params));
		Util.ifNotNull(getRequestParams(), params -> request.getParams().putAll(params));

		//2.生成实时内容
		Util.ifNotNull(mapper.getRandomGeneratorParams(), l -> l.forEach(r -> request.getParams().put(r.getName(), randomString(r, request.getCharset()))));
		Util.ifNotNull(getRandomGeneratorParams(), l -> l.forEach(r -> request.getParams().put(r.getName(), randomString(r, request.getCharset()))));

		Util.ifNotNull(mapper.getTimestampGeneratorParams(), l -> l.forEach(t -> request.getParams().put(t.getName(), timestamp(t))));
		Util.ifNotNull(getTimestampGeneratorParams(), l -> l.forEach(t -> request.getParams().put(t.getName(), timestamp(t))));

		Util.ifNotNull(mapper.getRandomGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), randomString(r, request.getCharset()))));
		Util.ifNotNull(getRandomGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), randomString(r, request.getCharset()))));


		//---以下跟运行参数有关

		//3.替换占位符


		//4.绑定运行参数

		return request;
	}

	private void replace(Request request, String placeholder, String replacement) {
		placeholder = "#{" + placeholder + "}";
		//替换URL
		if (Util.isNotEmpty(request.getUrl())) {
			request.setUrl(request.getUrl().replace(placeholder, replacement));
		}
		//替换请求体
		if (Util.isNotEmpty(request.getBody())) {
			request.setBody(request.getBody().replace(placeholder, replacement));
		}
		//替换请求头
		for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
			if (Util.isNotEmpty(entry.getValue())) {
				request.getHeaders().put(entry.getKey(), entry.getValue().replace(placeholder, replacement));
			}
		}
		//替换请求参数
		for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
			if (Util.isNotEmpty(entry.getValue())) {
				request.getParams().put(entry.getKey(), entry.getValue().replace(placeholder, replacement));
			}
		}
	}

	private String randomString(RandomGenerator randomGenerator, String charset) {
		String rawValue = Util.randomString(randomGenerator.getLength(), randomGenerator.getChars());
		return randomGenerator.isEncode() ? StrUtils.URLEncoder(rawValue, charset) : rawValue;
	}

	private String timestamp(TimestampGenerator timestampGenerator) {
		return timestampGenerator.getUnit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
	}

	private String buildUrl(String a, String b) {
		a = Util.isEmpty(a) ? "" : a;
		b = Util.isEmpty(b) ? "" : b;

		return b.startsWith("http") ? b : a + b;
	}
}
