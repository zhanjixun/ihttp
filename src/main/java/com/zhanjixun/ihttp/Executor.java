package com.zhanjixun.ihttp;

import java.io.IOException;

/**
 * HTTP执行器
 *
 * @author :zhanjixun
 * @date : 2019/04/13 13:49
 * @contact :zhanjixun@qq.com
 */
public interface Executor {
    /**
     * 执行http请求
     *
     * @param request
     * @return
     */
    Response execute(Request request, Request.Options options) throws IOException;

}
