package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.logging.ChromeLog;
import com.zhanjixun.ihttp.logging.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为一个接口Mapper指定使用的Log接收器,在一个类上面添加这个注解后就会开启log
 * 这里为了提供接口来支持多种打印方式
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {

    Class<? extends Log> value() default ChromeLog.class;

}
