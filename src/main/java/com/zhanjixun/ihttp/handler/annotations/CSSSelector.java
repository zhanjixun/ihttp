package com.zhanjixun.ihttp.handler.annotations;

import java.lang.annotation.*;

/**
 * 用css选择器来选择元素
 *
 * @author zhanjixun
 * @date 2020-06-11 15:01:35
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CSSSelector {

    String selector();

}
