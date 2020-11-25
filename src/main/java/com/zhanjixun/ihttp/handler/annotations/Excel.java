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
     * 解析的sheet 默认是第一个sheet
     *
     * @return
     */
    int sheet() default 0;

    /**
     * 起始数据行 默认第一行是标题 从第二行开始
     *
     * @return
     */
    int dataLine() default 1;

    /**
     * 类型
     *
     * @return
     */
    Class<?> returnType();
}

