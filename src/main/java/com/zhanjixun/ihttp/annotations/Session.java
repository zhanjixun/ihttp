package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(Sessions.class)
public @interface Session {

    String name();

    String value() default "";
}
