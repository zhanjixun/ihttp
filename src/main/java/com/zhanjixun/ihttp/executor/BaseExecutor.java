package com.zhanjixun.ihttp.executor;


import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;

/**
 * @author zhanjixun
 */
public abstract class BaseExecutor implements CookiesManager {

    public abstract Response execute(Request request);

}
