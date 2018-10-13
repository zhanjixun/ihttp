package com.zhanjixun.ihttp.test.hao6v;

import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;

/**
 * @author :zhanjixun
 * @date : 2018/10/3 0:22
 */
@UserAgent("zhanjixun@qq.com")
@Header(name = "zhanjixun", value = "z")
@Header(name = "zhanjixun", value = "z1")
public interface Hao6v {

    @GET
    @URL("http://www.hao6v.com/gvod/zx.html")
    @ResponseCharset("gb2312")
    Response gvod();

}
