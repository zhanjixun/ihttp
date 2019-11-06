package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.domain.Configuration;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Log4j
public abstract class BaseExecutor implements Executor {

	protected final Configuration configuration;
	@Getter
	protected final CookiesStore cookiesStore;

	public BaseExecutor(Configuration configuration, CookiesStore cookiesStore) {
		this.configuration = configuration;
		this.cookiesStore = cookiesStore;
	}

	@Override
	public Response execute(Request request) throws IOException {
		Response response;
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
			default:
				throw new RuntimeException("未能识别的HTTP请求方法：" + request.getMethod());
		}
		log.debug(String.join(" ", StringUtils.rightPad(request.getMethod(), 4), String.valueOf(response.getStatus()), request.getUrl()));
		//do something after http execute
		return response;
	}

	protected abstract Response doGetMethod(Request request) throws IOException;

	protected abstract Response doPostMethod(Request request) throws IOException;

	protected abstract Response doDeleteMethod(Request request) throws IOException;

	protected abstract Response doPutMethod(Request request) throws IOException;

}
