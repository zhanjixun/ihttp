package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.parsing.SwitchType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定几个可以动态切换的url地址
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BalanceURL {

    String[] value() default {};

    SwitchType switchType() default SwitchType.ORDER;

}
