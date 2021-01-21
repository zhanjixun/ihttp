package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestHeader.List.class)
public @interface RequestHeader {

    String name();

    String value() default "";

    //指定多个时使用
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        RequestHeader[] value();
    }
}
