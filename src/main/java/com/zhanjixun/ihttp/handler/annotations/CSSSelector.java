package com.zhanjixun.ihttp.handler.annotations;

import com.zhanjixun.ihttp.handler.data.SelectType;

import java.lang.annotation.*;

/**
 * 用css选择器来选择元素
 *
 * @author zhanjixun
 * @date 2020-06-11 15:01:35
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CSSSelector {
    /**
     * 状态码
     *
     * @return
     */
    int[] status() default {200};

    String selector();

    SelectType selectType();

    String attr() default "";

}
