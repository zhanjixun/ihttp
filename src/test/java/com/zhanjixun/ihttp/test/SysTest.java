package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.*;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SysTest {

	private Hao6vMapper mapper = IHTTP.getMapper(Hao6vMapper.class);

	@Test
	public void name() {
		Response index = mapper.index();
		System.out.println(index.getText());
	}

	@URL("http://www.hao6v.com")
	@ResponseCharset("gbk2312")
	interface Hao6vMapper {

		@GET
		@URL("/index.html")
		@UserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
		@Header(name = "from", value = "zhanjixun@qq.com")
		Response index();


	}
}
