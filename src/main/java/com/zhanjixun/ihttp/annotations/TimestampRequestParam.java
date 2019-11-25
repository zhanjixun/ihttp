package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 时间戳参数 添加此注解会为请求添加参数 name=时间戳
 *
 * @author :zhanjixun
 * @date : 2019/04/19 09:36
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TimestampRequestParam.List.class)
public @interface TimestampRequestParam {
	/**
	 * 参数名称
	 *
	 * @return
	 */
	String name();

	/**
	 * 时间单位
	 *
	 * @return
	 */
	TimeUnit unit() default TimeUnit.MILLISECONDS;

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		TimestampRequestParam[] value();
	}
}
