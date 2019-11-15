package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 断言一个方法的状态码
 *
 * @author :zhanjixun
 * @date : 2018/10/26 11:50
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertStatusCode {

	int[] value();

}
