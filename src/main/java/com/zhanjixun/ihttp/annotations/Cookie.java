package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 语义化请求
 * 添加请求头Cookie
 *
 * @author zhanjixun
 * @date 2020-12-17 14:03:24
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cookie {

    String value() default "";

}
