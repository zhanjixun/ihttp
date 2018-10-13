package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 占位符:在其他注解中使用#{paramName} 然后在java方法参数在添加这个注解 为其他注解替换占位符内容
 * 可以替换URL、StringBody、所有请求头的value以及所有请求参数的value
 *
 * @author :zhanjixun
 * @date : 2018/10/14 0:21
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Placeholder {

    String value();

}
