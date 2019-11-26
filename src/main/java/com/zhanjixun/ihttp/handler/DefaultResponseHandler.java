package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;

import java.lang.reflect.Method;

/**
 * @author :zhanjixun
 * @date : 2019/11/26 11:05
 * @contact :zhanjixun@qq.com
 */
public class DefaultResponseHandler implements ResponseHandler {

	@Override
	public Object handle(Method method, MapperMethod mapperMethod, Response response) {
		Class<?> returnType = method.getReturnType();
		if (returnType.getName().equals("void")) {
			return null;
		}

		if (returnType == Response.class) {
			return response;
		}

		return response;
	}

}
