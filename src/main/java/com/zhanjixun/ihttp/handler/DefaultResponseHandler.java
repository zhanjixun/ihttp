package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.handler.annotations.CSSSelector;
import com.zhanjixun.ihttp.handler.annotations.JsonPath;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * @author :zhanjixun
 * @date : 2019/11/26 11:05
 * @contact :zhanjixun@qq.com
 */
public class DefaultResponseHandler implements ResponseHandler {

    @Override
    public Object handle(Method method, MapperMethod mapperMethod, Response response) {
        Class<?> returnType = mapperMethod.getReturnType();
        String contentType = response.getContentType();

        if (returnType.getName().equals("void")) {
            return null;
        }
        //如果是Response类型 直接返回
        if (Response.class.isAssignableFrom(returnType)) {
            return response;
        }
        //解析图片类型
        if (Image.class.isAssignableFrom(returnType)) {
            return new ImageResponseHandler().handle(method, mapperMethod, response);
        }
        //含有JsonPath注解 或者内容为json 将被认为是json解析
        if (method.isAnnotationPresent(JsonPath.class) || contentType.contains("json")) {
            return new JsonResponseHandler().handle(method, mapperMethod, response);
        }
        //含有CSSSelector注解 或者内容为html 将被认为是html解析
        if (method.isAnnotationPresent(CSSSelector.class) || contentType.contains("html")) {
            return new HtmlResponseHandler().handle(method, mapperMethod, response);
        }
        return response;
    }


}
