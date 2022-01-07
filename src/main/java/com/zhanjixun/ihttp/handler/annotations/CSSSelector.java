package com.zhanjixun.ihttp.handler.annotations;

import com.zhanjixun.ihttp.handler.enums.SelectType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用css选择器来选择元素
 *
 * @author zhanjixun
 * @date 2020-06-11 15:01:35
 */
@Documented
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CSSSelector {
    /**
     * 选择器
     *
     * @return
     */
    String selector();

    /**
     * 选取类型
     *
     * @return
     */
    SelectType selectType();

    /**
     * 选取属性值
     *
     * @return
     */
    String attr() default "";

    /**
     * 类型
     *
     * @return
     */
    Class<?> returnType();
}
