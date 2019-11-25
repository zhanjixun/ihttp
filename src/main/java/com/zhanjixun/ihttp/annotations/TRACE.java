package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用TRACE请求:
 * 回显服务器收到的请求，主要用于测试或诊断。
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:46
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TRACE {

	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 */
	String charset() default "UTF-8";

}
