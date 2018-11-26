package com.zhanjixun.ihttp.test.hao6v;

import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhanjixun.ihttp.domain.Cookie;

/**
 * @author :zhanjixun
 * @date : 2018/10/3 0:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" })
public class Demo {

	@Autowired
	private Hao6v hao6v;

	@Test
	public void name() throws Exception {
		hao6v.gvod().ok(response -> {
			for (Element li : response.getDocument().select("ul.list li")) {
				String span = li.select("span").text();
				String name = li.select("a").text();
				System.out.println(String.format("%s\t%s", span, name));
			}
		});

		
	}
}
