package com.zhanjixun.ihttp.annotations;

/**
 * 断言一个方法的状态码
 *
 * @author :zhanjixun
 * @date : 2018/10/26 11:50
 */
public @interface AssertStatusCode {

    int[] value();

    String errorMessage() default "";

}
