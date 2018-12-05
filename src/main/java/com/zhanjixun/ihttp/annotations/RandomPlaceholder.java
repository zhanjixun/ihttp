package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 占位符:在其他注解中使用#{paramName} 然后在java方法参数在添加这个注解 为其他注解替换占位符内容
 * 可以替换URL、StringBody、所有请求头的value以及所有请求参数的value
 *
 * @author :zhanjixun
 * @date : 2018/10/21 0:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RandomPlaceholderRepeatable.class)
public @interface RandomPlaceholder {
    /**
     * paramName
     *
     * @return
     */
    String name();

    /**
     * 长度
     *
     * @return
     */
    int length();

    /**
     * 字符范围
     *
     * @return
     */
    String chars() default "abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ0123456789";

    /**
     * 是否进行编码
     *
     * @return
     * @see GET#charset()
     * @see POST#charset()
     */
    boolean encode() default false;
}