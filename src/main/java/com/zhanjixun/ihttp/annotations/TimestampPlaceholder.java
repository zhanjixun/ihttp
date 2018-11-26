package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 时间戳占位符
 *
 * @author :zhanjixun
 * @date : 2018/11/26 14:52
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimestampPlaceholder {
    /**
     * paramName
     *
     * @return
     */
    String name();

    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
