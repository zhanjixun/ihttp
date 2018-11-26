package com.zhanjixun.ihttp.result.html;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author :zhanjixun
 * @date : 2018/8/20 14:41
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectResult {

    /**
     * 一行封装的结果类型
     *
     * @return
     */
    Class<?> itemType();
}
