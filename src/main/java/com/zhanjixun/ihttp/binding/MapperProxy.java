package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.executor.BaseExecutor;

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
        return executor.execute(request);
    }

}
