package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.exception.AssertStatusCodeException;

import java.lang.annotation.*;

/**
 * 断言一个方法的状态码
 *
 * @author :zhanjixun
 * @date : 2018/10/26 11:50
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertStatusCode {
    /**
     * 指定能接受的所有状态码
     * 若接口返回状态码不在这个范围则抛出异常
     *
     * @return
     * @see AssertStatusCodeException
     */
    int[] value();

}
