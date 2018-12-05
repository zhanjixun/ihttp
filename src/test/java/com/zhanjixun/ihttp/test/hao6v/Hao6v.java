package com.zhanjixun.ihttp.test.hao6v;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.executor.JavaExecutor;

/**
 * @author :zhanjixun
 * @date : 2018/10/3 0:22
 */
@UserAgent("zhanjixun@qq.com")
@HttpExecutor(JavaExecutor.class)
public interface Hao6v extends CookiesStore {

    @GET
    @URL("http://www.hao6v.com/gvod/zx.html")
    @AssertStatusCode(200)
    Response gvod();

}
