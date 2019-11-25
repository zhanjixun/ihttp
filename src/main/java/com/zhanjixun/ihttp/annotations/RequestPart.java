package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 注解在参数上 参数类型必须为File或者String
 *
 * @author zhanjixun
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestPart.List.class)
public @interface RequestPart {

	String name();

	String value() default "";

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		RequestPart[] value();
	}
}
