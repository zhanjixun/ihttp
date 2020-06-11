package com.zhanjixun.ihttp.handler.annotations;

import java.lang.annotation.*;

/**
 * 解析Excel类型返回值
 *
 * @author zhanjixun
 * @date 2020-06-11 13:50:34
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {
    /**
     * 状态码
     *
     * @return
     */
    int[] status() default {200};

    /**
     * 解析的sheet 默认是第一个sheet
     *
     * @return
     */
    String sheet() default "sheet1";

}
