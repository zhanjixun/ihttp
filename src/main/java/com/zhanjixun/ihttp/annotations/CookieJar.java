package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 实现多个Mapper公用Cookie
 *
 * @author :zhanjixun
 * @date : 2019/04/13 11:16
 * @contact :zhanjixun@qq.com
 */
@Documented
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
