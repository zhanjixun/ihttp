package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用HEAD请求:
 * 类似于 GET 请求，只不过返回的响应中没有具体的内容，用于获取报头
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:45
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HEAD {
	/**
	 * 设置HTTP方法是否应自动遵循HTTP重定向
	 */
	boolean followRedirects() default true;

	/**
	 * 定义用于编码内容体的字符集
	 */
	String charset() default "UTF-8";
}
