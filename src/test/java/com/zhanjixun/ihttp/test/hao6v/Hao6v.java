package com.zhanjixun.ihttp.test.hao6v;

import com.zhanjixun.ihttp.CookiesManager;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.result.html.ListResult;

/**
 * @author :zhanjixun
 * @date : 2018/10/3 0:22
 */
@UserAgent("zhanjixun@qq.com")
@Header(name = "zhanjixun", value = "z")
@Header(name = "zhanjixun", value = "z1")
public interface Hao6v extends CookiesManager {

	@GET
	@URL("http://www.hao6v.com/gvod/zx.html")
	@ResponseCharset("gb2312")
	@ListResult(cssSelector = "ul.list li", itemType = Bean.class)
	Response gvod();

}
