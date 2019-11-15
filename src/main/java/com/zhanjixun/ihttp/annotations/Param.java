package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 使用在参数上时 参数类型需为String
 *
 * @author zhanjixun
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Param.List.class)
public @interface Param {

	String name();

	String value() default "";

	/**
	 * 是否进行编码
	 *
	 * @return
	 * @see GET#charset()
	 * @see POST#charset()
	 */
	boolean encode() default false;

	//指定多个时使用
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		Param[] value();
	}
}
