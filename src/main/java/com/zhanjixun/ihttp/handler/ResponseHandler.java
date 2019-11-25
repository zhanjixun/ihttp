package com.zhanjixun.ihttp.handler;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.binding.MapperMethod;

import java.lang.reflect.Method;

/**
 * 返回处理：适配返回值类型
 *
 * @author :zhanjixun
 * @date : 2019/11/25 20:42
 */
public interface ResponseHandler {


    Object handle(Method method, MapperMethod mapperMethod, Response response);

}
