package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 使用在参数上时 参数类型需为String
 *
 * @author zhanjixun
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ParamRepeatable.class)
public @interface Param {

    String name();

    String value() default "";

    /**
     * 是否进行编码
     *
     * @return
     */
    boolean encode() default false;

    /**
     * url编码的字符编码
     *
     * @return
     */
    String charset() default "UTF-8";

}
