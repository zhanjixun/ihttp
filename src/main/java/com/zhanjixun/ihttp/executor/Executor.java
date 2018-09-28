package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;

/**
 * 执行http请求的工具
 *
 * @author zhanjixun
 * @time 2018年3月13日 15:37:32
 */
public interface Executor {

    /**
     * 执行http请求
     *
     * @param request
     * @return
     */
    Response execute(Request request);

}
