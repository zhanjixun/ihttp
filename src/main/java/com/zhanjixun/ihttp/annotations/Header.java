package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Header.List.class)
public @interface Header {

	String name();

	String value() default "";

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		Header[] value();
	}
}
