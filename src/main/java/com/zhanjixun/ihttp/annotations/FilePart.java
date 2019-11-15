package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.domain.ByteArrayFile;

import java.lang.annotation.*;

/**
 * 注解在参数上 参数类型必须为File或者String
 *
 * @author zhanjixun
 * @see ByteArrayFile
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FilePart.List.class)
public @interface FilePart {

	String name();

	String value() default "";

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		FilePart[] value();
	}
}
