package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 指定请求URL
 * 可以使用在类、方法和形参上，优先级递增，如果某个url以http开头，会忽略前url
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface URL {

	String value() default "";

}
