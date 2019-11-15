package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.executor.ComponentsHttpClientExecutor;
import com.zhanjixun.ihttp.executor.Executor;

import java.lang.annotation.*;

/**
 * 指定http执行器
 *
 * @author :zhanjixun
 * @date : 2018/11/26 13:35
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpExecutor {

	Class<? extends Executor> value() default ComponentsHttpClientExecutor.class;

}
