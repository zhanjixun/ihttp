package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个方法使用DELETE请求
 *
 * @author :zhanjixun
 * @date : 2019/11/06 11:53
 * @contact :zhanjixun@qq.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {

	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 */
	String charset() default "UTF-8";

}
