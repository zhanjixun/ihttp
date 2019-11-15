package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 设置请求正文，直接将字符串写入请求体
 * 可用类型，基本数据类型 String
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringBody {

	String value() default "";

	/**
	 * 是否进行编码
	 *
	 * @return
	 * @see GET#charset()
	 * @see POST#charset()
	 */
	boolean encode() default false;
}
