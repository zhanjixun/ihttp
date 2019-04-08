package com.zhanjixun.ihttp.result.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将cssSelector获取到的所有元素，逐行封装成itemType类型返回
 * 方法上接收的结果类型为List<itemType>
 *
 * @author :zhanjixun
 * @date : 2018/8/20 14:43
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListResult {
    /**
     * css选择器
     *
     * @return
     */
    String cssSelector();

    /**
     * 一行封装的结果类型
     *
     * @return
     */
    Class<?> itemType();
}

