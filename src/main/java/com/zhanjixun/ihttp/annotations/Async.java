package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 声明一个方法异步执行
 *
 * @author zhanjixun
 * @date 2021-04-07 14:26:47
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {

}
