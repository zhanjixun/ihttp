package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.executor.BaseExecutor;
import com.zhanjixun.ihttp.executor.CommonsHttpClientExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定http执行器
 *
 * @author :zhanjixun
 * @date : 2018/11/26 13:35
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpExecutor {

    Class<? extends BaseExecutor> value() default CommonsHttpClientExecutor.class;

}
