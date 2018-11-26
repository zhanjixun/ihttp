package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.AssertStatusCode;
import com.zhanjixun.ihttp.executor.BaseExecutor;
import org.springframework.util.Assert;

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
    private final BaseExecutor executor;

    public MapperProxy(Mapper mapper, BaseExecutor executor) {
        this.mapper = mapper;
        this.executor = executor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (CookiesManager.class.equals(method.getDeclaringClass())) {
            return method.invoke(executor, args);
        }
        Request request = mapper.getRequest(method.getName(), args);
        Response response = executor.execute(request);

        //断言状态码
        AssertStatusCode annotation = method.getAnnotation(AssertStatusCode.class);
        if (annotation != null) {
            int[] codes = annotation.value();
            String errorMessage = String.format("%s.%s没有返回需要的状态码(%d)", method.getDeclaringClass().getName(), method.getName(), response.getStatus());
            Assert.isTrue(Arrays.asList(codes).contains(response.getStatus()), errorMessage);
        }

        return response;
    }

}
