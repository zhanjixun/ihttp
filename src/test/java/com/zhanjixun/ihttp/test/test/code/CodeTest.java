package com.zhanjixun.ihttp.test.test.code;

import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import com.zhanjixun.ihttp.test.TestMapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Test;

import java.io.StringWriter;

/**
 * @author :zhanjixun
 * @date : 2019/12/02 15:59
 * @contact :zhanjixun@qq.com
 */
public class CodeTest {


	@Test
	public void name() {
		Mapper parse = new AnnotationParser(TestMapper.class).parse();

		VelocityContext velocityContext = new VelocityContext();
		velocityContext.put("mapper", parse);
		velocityContext.put("packageName", "com.zhanjixun.mapper");
		velocityContext.put("className", TestMapper.class.getSimpleName());

		StringWriter writer = new StringWriter();

		Template template = Velocity.getTemplate("src/test/resources/template/Mapper.java.vm", "utf-8");
		template.merge(velocityContext, writer);

		System.out.println(writer.toString());
	}


}
