package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 为请求添加 Accept-Encoding 请求头
 * <p>
 * 请求头添加：
 * Accept-Encoding = $value
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptEncoding {

	/**
	 * 请求头Accept-Encoding的值
	 *
	 * @return
	 */
	String value() default "";
}
