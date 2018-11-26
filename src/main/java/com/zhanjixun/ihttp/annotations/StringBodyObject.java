package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在方法参数中指定一个对象来动态生成json字符串，作为http请求体
 * 可用在POJO类、集合或者Map对象上
 *
 * @author :zhanjixun
 * @date : 2018/10/24 11:25
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringBodyObject {

}
