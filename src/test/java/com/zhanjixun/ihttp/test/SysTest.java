package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import com.zhanjixun.ihttp.annotations.GET;
import com.zhanjixun.ihttp.annotations.URL;
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

	interface Hao6vMapper {

		@GET
		@URL("http://www.hao6v.com/index.html")
		Response index();
	}
}
