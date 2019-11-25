package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用GET请求:
 * 请求指定的页面信息，并返回实体主体。
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GET {

	/**
	 * 设置HTTP方法是否应自动遵循HTTP重定向
	 *
	 * @return
	 */
	boolean followRedirects() default true;

	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 * @see Param#encode()
	 * @see Placeholder#encode()
	 */
	String charset() default "UTF-8";
}
