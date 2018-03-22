package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 使用在参数上时 参数类型需为String
 *
 * @author zhanjixun
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Params.class)
public @interface Param {

    String name();

    String value() default "";

}
