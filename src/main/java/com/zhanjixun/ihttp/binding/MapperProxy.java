package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.AssertStatusCode;
import com.zhanjixun.ihttp.exception.AssertStatusCodeException;
import com.zhanjixun.ihttp.executor.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 代理Mapper对象
 *
 * @author zhanjixun
 */
public class MapperProxy implements InvocationHandler {

	private final Mapper mapper;
	private final Executor executor;

	public MapperProxy(Mapper mapper, Executor executor) {
		this.mapper = mapper;
		this.executor = executor;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//Object方法执行
		if (Object.class.equals(method.getDeclaringClass())) {
			try {
				return method.invoke(this, args);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		//CookiesStore方法执行
		if (CookiesStore.class.equals(method.getDeclaringClass())) {
			return method.invoke(executor.getCookiesStore(), args);
		}
		Request request = mapper.getRequest(method.getName(), args);

		Response response = executor.execute(request);

		//断言状态码
		AssertStatusCode annotation = method.getAnnotation(AssertStatusCode.class);
		if (annotation != null) {
			int[] codes = annotation.value();
			if (Arrays.stream(codes).noneMatch(num -> num == response.getStatus())) {
				throw new AssertStatusCodeException("没有返回预期的状态码：" + response.getStatus());
			}
		}

		return response;
	}

}
