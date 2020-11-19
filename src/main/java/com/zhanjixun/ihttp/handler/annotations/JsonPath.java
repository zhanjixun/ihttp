package com.zhanjixun.ihttp.handler.annotations;

import com.zhanjixun.ihttp.handler.enums.ElementType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解析返回值的时候用于指定json解析路径
 *
 * @author zhanjixun
 * @date 2020-06-10 14:34:36
 * @see com.alibaba.fastjson.JSONPath
 */
@Documented
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPath {
    /**
     * 路径
     *
     * @return
     */
    String path();

    /**
     * 类型
     *
     * @return
     */
    Class<?> returnType();

    /**
     * 解析对象类型 对象（单个对象） 数组（集合）
     *
     * @return
     */
    ElementType elementType() default ElementType.OBJECT;
}
