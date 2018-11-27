package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只是为了让子注解可以多次使用
 *
 * @author :zhanjixun
 * @date : 2018/11/27 14:07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimestampPlaceholderRepeatable {
    TimestampPlaceholder[] value() default {};
}
