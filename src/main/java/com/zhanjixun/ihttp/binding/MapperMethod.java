package com.zhanjixun.ihttp.binding;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.FilePart;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.parsing.*;
import com.zhanjixun.ihttp.utils.StrUtils;
import com.zhanjixun.ihttp.utils.Util;
import lombok.Data;

import java.io.File;
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

	private String url;

	private String requestCharset;

	private String responseCharset;

	private Boolean followRedirects;

	//GET,POST,PUT,DELETE
	private String requestMethod;

	private List<Header> requestHeaders;

	private List<Param> requestParams;

	private List<FileParts> requestMultiParts;

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
		Util.ifNotNull(mapper.getRandomGeneratorParams(), l -> l.forEach(r -> request.getParams().put(r.getName(), random(r, request.getCharset()))));
		Util.ifNotNull(getRandomGeneratorParams(), l -> l.forEach(r -> request.getParams().put(r.getName(), random(r, request.getCharset()))));

		Util.ifNotNull(mapper.getTimestampGeneratorParams(), l -> l.forEach(t -> request.getParams().put(t.getName(), timestamp(t))));
		Util.ifNotNull(getTimestampGeneratorParams(), l -> l.forEach(t -> request.getParams().put(t.getName(), timestamp(t))));

		Util.ifNotNull(mapper.getRandomGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), random(r, request.getCharset()))));
		Util.ifNotNull(getRandomGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), random(r, request.getCharset()))));

		Util.ifNotNull(mapper.getTimestampGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), timestamp(r))));
		Util.ifNotNull(getTimestampGeneratorPlaceholders(), l -> l.forEach(r -> replace(request, r.getName(), timestamp(r))));

		//3.绑定运行参数
		for (MapperParameter mapperParameter : parameters) {
			Object arg = args[mapperParameter.getIndex()];
			if (mapperParameter.isURLAnnotated()) {
				request.setUrl(buildUrl(request.getUrl(), (String) arg));
			}
			if (Util.isNotEmpty(mapperParameter.getRequestParamNames())) {
				for (EncodableString requestParamName : mapperParameter.getRequestParamNames()) {
					Class<?> parameterType = mapperParameter.getParameterType();
					if (parameterType == String.class) {
						String value = requestParamName.encode() ? StrUtils.URLEncoder((String) arg, request.getCharset()) : (String) arg;
						request.getParams().put(requestParamName.getName(), value);
					} else if (parameterType == Map.class) {
						Map<String, Object> map = (Map) arg;
						String suffix = Util.isNotEmpty(requestParamName.getName()) ? requestParamName.getName() + "." : "";
						for (Map.Entry<String, Object> entry : map.entrySet()) {
							request.getParams().put(suffix + entry.getKey(), String.valueOf(entry.getValue()));
						}
					} else {
						//todo 内省

					}
				}
			}
			if (Util.isNotEmpty(mapperParameter.getRequestHeaderNames())) {
				for (String headerName : mapperParameter.getRequestHeaderNames()) {
					request.getHeaders().put(headerName, (String) arg);
				}
			}
			if (Util.isNotEmpty(mapperParameter.getRequestMultiPartNames())) {
				for (String multiPartName : mapperParameter.getRequestMultiPartNames()) {
					if (arg instanceof String) {
						request.getFileParts().put(multiPartName, new File((String) arg));
					} else if (arg instanceof File) {
						request.getFileParts().put(multiPartName, (File) arg);
					} else {
						throw new IllegalArgumentException("在方法的参数中使用" + FilePart.class.getName() + "时，被注解的参数类型必须为java.lang.String或者java.io.File");
					}
				}
			}
			if (mapperParameter.getRequestBody() != null) {
				EncodableObject requestBody = mapperParameter.getRequestBody();
				if (arg instanceof String) {
					request.setBody(requestBody.encode() ? StrUtils.URLEncoder((String) arg, request.getCharset()) : (String) arg);
				} else {
					String jsonBody = JSON.toJSONString(arg);
					request.setBody(requestBody.encode() ? StrUtils.URLEncoder(jsonBody, request.getCharset()) : jsonBody);
				}
			}
		}

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

	private String random(RandomGenerator randomGenerator, String charset) {
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
