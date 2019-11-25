package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.parsing.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class BaseExecutor implements Executor {

	protected final Configuration configuration;
	@Getter
	protected final CookiesStore cookiesStore;

	public BaseExecutor(Configuration configuration, CookiesStore cookiesStore) {
		this.configuration = configuration;
		this.cookiesStore = cookiesStore;
	}

	@Override
	public final Response execute(Request request) throws IOException {
		Response response;

		long startTime = System.currentTimeMillis();
		switch (request.getMethod()) {
			case "GET":
				response = doGetMethod(request);
				break;
			case "POST":
				response = doPostMethod(request);
				break;
			case "DELETE":
				response = doDeleteMethod(request);
				break;
			case "PUT":
				response = doPutMethod(request);
				break;
			case "HEAD":
				response = doHeadMethod(request);
				break;
			case "OPTIONS":
				response = doOptionsMethod(request);
				break;
			case "TRACE":
				response = doTraceMethod(request);
				break;
			case "PATCH":
				response = doPatchMethod(request);
				break;
			default:
				throw new RuntimeException("未能识别的HTTP请求方法：" + request.getMethod());
		}
		long endTime = System.currentTimeMillis();

		log.debug(request.getMethod() + " " + response.getStatus() + " [" + (endTime - startTime) + "s] " + request.getUrl());
		return response;
	}

	protected abstract Response doGetMethod(Request request) throws IOException;

	protected abstract Response doPostMethod(Request request) throws IOException;

	protected abstract Response doDeleteMethod(Request request) throws IOException;

	protected abstract Response doPutMethod(Request request) throws IOException;

	protected abstract Response doPatchMethod(Request request) throws IOException;

	protected abstract Response doTraceMethod(Request request) throws IOException;

	protected abstract Response doOptionsMethod(Request request) throws IOException;

	protected abstract Response doHeadMethod(Request request) throws IOException;

}
