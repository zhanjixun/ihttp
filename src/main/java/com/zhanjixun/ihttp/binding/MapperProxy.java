package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.PlaceholderManager;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.handler.ResponseHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理Mapper对象
 *
 * @author zhanjixun
 */
public class MapperProxy implements InvocationHandler {

    private final Mapper mapper;

    private final ResponseHandler responseHandler = new ResponseHandler();

    public MapperProxy(Mapper mapper) {
        this.mapper = mapper;
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
            return method.invoke(mapper.getExecutor().getCookiesStore(), args);
        }
        //PlaceholderManager方法执行
        if (PlaceholderManager.class.equals(method.getDeclaringClass())) {
            return method.invoke(mapper.getPlaceholderManager(), args);
        }

        MapperMethod mapperMethod = mapper.getMapperMethod(method.getName());
        Response response = mapperMethod.execute(args);

        return responseHandler.handle(method, mapperMethod, response);
    }

}
