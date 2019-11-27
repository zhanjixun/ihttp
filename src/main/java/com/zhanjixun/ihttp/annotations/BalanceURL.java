package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.parsing.SwitchType;

import java.lang.annotation.*;

/**
 * 指定几个可以动态切换的url地址
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BalanceURL {

	String[] value() default {};

	SwitchType switchType() default SwitchType.ORDER;

}
