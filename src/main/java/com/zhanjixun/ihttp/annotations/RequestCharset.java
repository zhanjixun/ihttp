package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置请求体的字符编码
 *
 * @author zhanjixun
 * @see Param#encode()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Deprecated
public @interface RequestCharset {

    String value() default "UTF-8";
}
