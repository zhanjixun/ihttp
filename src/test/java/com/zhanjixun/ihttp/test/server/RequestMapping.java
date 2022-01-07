package com.zhanjixun.ihttp.test.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 简易的MVC
 *
 * @author zhanjixun
 * @date 2021-04-14 16:39:14
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /**
     * 请求方法
     *
     * @return
     */
    String[] method() default {"GET", "POST"};

    /**
     * 派发路径
     *
     * @return
     */
    String value();

}
