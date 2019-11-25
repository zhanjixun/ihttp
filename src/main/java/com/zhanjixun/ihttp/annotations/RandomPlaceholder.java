package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 占位符:在其他注解中使用#{paramName} 然后在java方法参数在添加这个注解 为其他注解替换占位符内容
 * 可以替换URL、body、所有请求头的value以及所有请求参数的value
 *
 * @author :zhanjixun
 * @date : 2018/10/21 0:01
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RandomPlaceholder.List.class)
public @interface RandomPlaceholder {
	/**
	 * 占位符
	 *
	 * @return
	 */
	String name();

	/**
	 * 长度
	 *
	 * @return
	 */
	int length();

	/**
	 * 字符范围
	 *
	 * @return
	 */
	String chars() default "abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ0123456789";

	/**
	 * 是否进行编码
	 */
	boolean encode() default false;

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		RandomPlaceholder[] value();
	}

}