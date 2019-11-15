package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Referer {
	String value() default "";
}
