package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用PATCH请求:
 * 是对 PUT 方法的补充，用来对已知资源进行局部更新 。
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:46
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PATCH {
}
