package com.zhanjixun.ihttp.result.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author :zhanjixun
 * @date : 2018/8/20 15:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LongResult {
    /**
     * css选择器
     *
     * @return
     */
    String cssSelector();

    /**
     * css选择器获取第几个结果，默认第一个
     *
     * @return
     */
    int index() default 0;

    /**
     * 选取数据部分
     *
     * @return
     */
    ResultType type();
}