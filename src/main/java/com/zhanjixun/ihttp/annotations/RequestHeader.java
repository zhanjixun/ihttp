package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 请求头
 *
 * @return
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestHeader.List.class)
public @interface RequestHeader {
    /**
     * 请求头名称
     *
     * @return
     */
    String name();

    /**
     * 请求头的值
     *
     * @return
     */
    String value() default "";

    //指定多个时使用
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        RequestHeader[] value();
    }
}
