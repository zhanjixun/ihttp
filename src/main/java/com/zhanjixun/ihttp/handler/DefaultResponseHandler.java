package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;

/**
 * @author :zhanjixun
 * @date : 2019/11/26 11:05
 * @contact :zhanjixun@qq.com
 */
public class DefaultResponseHandler implements ResponseHandler {

    @Override
    public Object handle(MapperMethod mapperMethod, Response response) {
        Class<?> returnType = mapperMethod.getReturnType();
        if (returnType.getName().equals("void")) {
            return null;
        }

        if (returnType == Response.class) {
            return response;
        }
        //TODO 处理返回值类型

        return response;
    }

}
