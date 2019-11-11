package com.zhanjixun.ihttp.annotations;

/**
 * 标识一个方法使用PUT请求
 *
 * @author :zhanjixun
 * @date : 2019/11/06 11:54
 * @contact :zhanjixun@qq.com
 */
public @interface PUT {

	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 */
	String charset() default "UTF-8";

}