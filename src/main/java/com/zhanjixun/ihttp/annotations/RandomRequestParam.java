package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 随机参数
 *
 * @author :zhanjixun
 * @date : 2019/04/19 09:52
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RandomRequestParam.List.class)
public @interface RandomRequestParam {
    /**
     * 参数名
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
     * 前缀
     *
     * @return
     */
    String prefix() default "";

    /**
     * 尾缀
     *
     * @return
     */
    String postfix() default "";

    /**
     * 是否进行编码
     *
     * @return
     * @see GET#charset()
     * @see POST#charset()
     */
    boolean encode() default false;

    //指定多个时使用
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        RandomRequestParam[] value();
    }
}
