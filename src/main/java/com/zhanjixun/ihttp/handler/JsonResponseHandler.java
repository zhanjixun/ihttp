package com.zhanjixun.ihttp.handler;

import com.alibaba.fastjson.JSONPath;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;
import com.zhanjixun.ihttp.utils.ReflectUtils;

import java.lang.reflect.Method;

/**
 * @author zhanjixun
 * @date 2020-06-11 13:53:08
 */
public class JsonResponseHandler implements ResponseHandler {
    @Override
    public Object handle(Method method, MapperMethod mapperMethod, Response response) {
        Class<?> returnType = mapperMethod.getReturnType();

        JsonPath jsonPath = method.getAnnotation(JsonPath.class);
        if (jsonPath != null) {
            Object obj = JSONPath.read(response.getText(), jsonPath.path());
            //string及基本类型
            if (ReflectUtils.isStringOrPrimitive(returnType)) {
                return obj;
            }

            //TODO
        }
        return null;
    }


}
