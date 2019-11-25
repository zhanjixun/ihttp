package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用OPTIONS请求:
 * 允许客户端查看服务器的性能。
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:46
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OPTIONS {
	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 * @see Param#encode()
	 * @see Placeholder#encode()
	 */
	String charset() default "UTF-8";
}
