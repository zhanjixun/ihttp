package com.zhanjixun.ihttp.test;

import com.zhanjixun.ihttp.IHTTP;
import com.zhanjixun.ihttp.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:13
 */
public class SysTest {

	private TestMapper mapper = IHTTP.getMapper(TestMapper.class);

	private NettyServer nettyServer = new NettyServer(8088);

	@Before
	public void startServer() {
		nettyServer.start();
	}

	@After
	public void shutdownServer() {
		nettyServer.shutdownGracefully();
	}

	@Test
	public void name() {
		Response index = mapper.index();
		Document parse = Jsoup.parse(index.getText());

		System.out.println();
	}

}
