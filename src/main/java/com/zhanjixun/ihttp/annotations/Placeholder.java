package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 占位符:在其他注解中使用#{paramName} 然后在java方法参数在添加这个注解 为其他注解替换占位符内容
 * 可以替换URL、body、所有请求头的value以及所有请求参数的value
 * 支持类型：String和基本数据类型以及其封装类
 *
 * @author :zhanjixun
 * @date : 2018/10/14 0:21
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Placeholder {
	/**
	 * 占位符
	 */
	String value();

	/**
	 * 是否进行编码
	 */
	boolean encode() default false;

}
