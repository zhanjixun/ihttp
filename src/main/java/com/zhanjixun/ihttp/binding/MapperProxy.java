package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.executor.Executor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        Request request = mapper.getRequest(method.getName(), args);
        return executor.execute(request);
    }

}
