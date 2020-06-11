package com.zhanjixun.ihttp.handler.annotations;

import java.lang.annotation.*;

/**
 * 用xpath选择元素
 *
 * @author zhanjixun
 * @date 2020-06-11 15:02:51
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPath {

    String path();
}
