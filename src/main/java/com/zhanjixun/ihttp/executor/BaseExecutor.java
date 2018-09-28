package com.zhanjixun.ihttp.executor;


import com.google.common.base.Preconditions;
import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.constant.HttpMethod;

import java.util.Objects;

/**
 * @author zhanjixun
 */
public abstract class BaseExecutor implements CookiesManager {

    public Response execute(Request request) {
        Preconditions.checkArgument(Objects.nonNull(request), "请求不能为空");
        if (request.getMethod().equals(HttpMethod.GET.name())) {
            return doGetMethod(request);
        }
        if (request.getMethod().equals(HttpMethod.POST.name())) {
            return doPostMethod(request);
        }
        if (request.getMethod().equals(HttpMethod.DELETE.name())) {
            return doDeleteMethod(request);
        }
        if (request.getMethod().equals(HttpMethod.HEAD.name())) {
            return doHeadMethod(request);
        }
        if (request.getMethod().equals(HttpMethod.PUT.name())) {
            return doPutMethod(request);
        }
        throw new RuntimeException("未能识别的http请求方法：" + request.getMethod());
    }

    protected Response doGetMethod(Request request) {
        return null;
    }

    protected Response doPostMethod(Request request) {
        return null;
    }

    protected Response doDeleteMethod(Request request) {
        return null;
    }

    protected Response doHeadMethod(Request request) {
        return null;
    }

    protected Response doPutMethod(Request request) {
        return null;
    }

}
