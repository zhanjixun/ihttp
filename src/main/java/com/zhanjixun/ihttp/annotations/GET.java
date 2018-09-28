package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个方法使用GET请求
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GET {

    /**
     * 设置HTTP方法是否应自动遵循HTTP重定向
     *
     * @return
     */
    boolean followRedirects() default true;

    /**
     * 定义用于编码内容体的字符集
     *
     * @return
     */
    String charset() default "UTF-8";
}
