package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.AssertStatusCode;
import com.zhanjixun.ihttp.annotations.ResponseCharset;
import com.zhanjixun.ihttp.exception.AssertStatusCodeException;
import com.zhanjixun.ihttp.handler.ResponseHandler;
import com.zhanjixun.ihttp.utils.ReflectUtils;

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

        MapperMethod mapperMethod = mapper.getMapperMethod(method.getName());
        Response response = mapperMethod.execute(args);

        //手动指明返回值解析的字符编码
        ResponseCharset responseCharset = ReflectUtils.getAnnotationFromMethodOrClass(method, ResponseCharset.class);
        if (responseCharset != null) {
            response.setCharset(responseCharset.value());
        }

        //断言状态码
        AssertStatusCode assertStatusCode = ReflectUtils.getAnnotationFromMethodOrClass(method, AssertStatusCode.class);
        if (assertStatusCode != null && assertStatusCode.value().length > 0) {
            if (Arrays.stream(assertStatusCode.value()).noneMatch(code -> code == response.getStatus())) {
                throw new AssertStatusCodeException("没有返回预期的状态码：" + response.getStatus());
            }
        }

        return responseHandler.handle(method, mapperMethod, response);
    }

}
