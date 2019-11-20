package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.RetryPolicy;

import java.lang.annotation.*;

/**
 * 标记在一个Mapper接口或者Mapper方法上
 * 1.标记在Mapper方法上 该方法支持重试功能
 * 2.标记在Mapper接口上 所有方法都支持重试功能
 * 3.throwable和policy至少满足一个即可触发重试
 *
 * @author :zhanjixun
 * @date : 2019/11/20 21:55
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {

    /**
     * 可重试的异常类型
     */
    Class<? extends Throwable> throwable() default RuntimeException.class;

    /**
     * 重试策略
     *
     * @return
     */
    Class<? extends RetryPolicy>[] policy() default {};

    /**
     * 最大尝试次数（包括第一次失败），默认为3
     */
    int maxAttempts() default 3;

    /**
     * 重试延迟默认不延迟，单位毫秒
     */
    long delay() default 0;

    /**
     * 用于计算下一个延迟延迟的乘数(大于0生效)
     */
    long multiplier() default 0;

}
