package com.zhanjixun.ihttp.binding;

import com.google.common.primitives.Ints;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.AssertStatusCode;
import com.zhanjixun.ihttp.executor.BaseExecutor;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
            String errorMessage = String.format("%s.%s状态码断言异常(需要%s,返回%d)", method.getDeclaringClass().getName(), method.getName(), Ints.asList(codes), response.getStatus());
            Assert.isTrue(Ints.contains(codes, response.getStatus()), errorMessage);
        }

        return response;
    }

}
