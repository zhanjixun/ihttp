package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 时间戳参数 添加此注解会为请求添加参数 name=时间戳
 *
 * @author :zhanjixun
 * @date : 2019/04/19 09:36
 * @contact :zhanjixun@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TimestampParamRepeatable.class)
public @interface TimestampParam {
    /**
     * 参数名称
     *
     * @return
     */
    String name();

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
