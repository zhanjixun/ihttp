package com.zhanjixun.ihttp.test.hao6v;

import com.zhanjixun.ihttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class Demo {

	@Autowired
	private Hao6v hao6v;

	@Test
	public void name() throws Exception {
		Response response = hao6v.gvod();
		for (Element li : response.getDocument().select("ul.list li")) {
			String span = li.select("span").text();
			String name = li.select("a").text();
			System.out.println(String.format("%s\t%s", span, name));
		}
	}
}
