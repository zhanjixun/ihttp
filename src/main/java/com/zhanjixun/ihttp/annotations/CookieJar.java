package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现多个Mapper公用Cookie
 *
 * @author :zhanjixun
 * @date : 2019/04/13 11:16
 * @contact :zhanjixun@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CookieJar {
	/**
	 * 指定一个shareID
	 * 同一个ID的Mapper之间共享Cookie
	 *
	 * @return
	 */
	String value();

}
