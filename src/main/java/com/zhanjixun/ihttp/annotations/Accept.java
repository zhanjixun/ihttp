package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 为请求添加 Accept 请求头
 * <p>
 * 请求头添加：
 * Accept = $value
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Accept {

	/**
	 * 请求头Accept的值
	 *
	 * @return
	 */
	String value() default "";
}
