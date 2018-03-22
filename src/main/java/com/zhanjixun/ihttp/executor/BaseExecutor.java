package com.zhanjixun.ihttp.executor;


import com.google.common.base.Preconditions;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.constant.HttpMethod;

import java.util.Objects;

/**
 * @author zhanjixun
 */
public class BaseExecutor implements Executor {

    @Override
    public Response execute(Request request) {
        Preconditions.checkArgument(Objects.nonNull(request), "请求不能为空");
        if (request.getMethod().equals(HttpMethod.GET.getName())) {
            return doGetMethod(request);
        } else if (request.getMethod().equals(HttpMethod.POST.getName())) {
            return doPostMethod(request);
        } else if (request.getMethod().equals(HttpMethod.DELETE.getName())) {
            return doDeleteMethod(request);
        } else if (request.getMethod().equals(HttpMethod.HEAD.getName())) {
            return doHeadMethod(request);
        } else if (request.getMethod().equals(HttpMethod.PUT.getName())) {
            return doPutMethod(request);
        } else {
            throw new RuntimeException("未能识别的http请求方法：" + request.getMethod());
        }
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
