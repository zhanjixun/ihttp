package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用DELETE请求
 *
 * @author :zhanjixun
 * @date : 2019/11/06 11:53
 * @contact :zhanjixun@qq.com
 * @see GET
 * @see POST
 * @see PUT
 */
@Documented
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
