package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 禁用cookie
 * 标记注解，使用在一个类或者方法上，声明该类或方法不使用Cookie
 *
 * @author :zhanjixun
 * @date : 2018/12/15 11:11
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableCookie {
}
