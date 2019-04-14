package com.zhanjixun.ihttp.executor;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.Response;

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
    Response execute(Request request);

    /**
     * 获取Cookie接口
     *
     * @return
     */
    CookiesStore getCookiesStore();
}
