package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.utils.Util;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SysTest {

	@Test
	public void name() {
		System.out.println(Util.randomString(12, "123456789"));
	}

}
