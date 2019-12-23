package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 时间戳占位符
 *
 * @author :zhanjixun
 * @date : 2018/11/26 14:52
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TimestampPlaceholder.List.class)
public @interface TimestampPlaceholder {
    /**
     * 需要替换字符的占位符#{name}
     *
     * @return
     */
    String name();

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    //指定多个时使用
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        TimestampPlaceholder[] value();
    }
}
