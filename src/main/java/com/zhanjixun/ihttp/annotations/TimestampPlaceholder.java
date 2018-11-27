package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 时间戳占位符
 *
 * @author :zhanjixun
 * @date : 2018/11/26 14:52
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TimestampPlaceholderRepeatable.class)
public @interface TimestampPlaceholder {
    /**
     * paramName
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
}
