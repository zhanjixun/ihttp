package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(Headers.class)
public @interface Header {

    String name();

    String value() default "";

}
