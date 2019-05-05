package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手动指定解析Response时候使用的字符编码
 * 如不指定，将自动从Content-Type中获取
 * 一般如果网页响应头中Content-Type没有charset
 * 需要使用这个注解
 *
 * @author :zhanjixun
 * @date : 2019/05/05 10:35
 * @contact :zhanjixun@qq.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseCharset {

    String value();

}
