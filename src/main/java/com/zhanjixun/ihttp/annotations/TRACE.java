package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用TRACE请求:
 * 回显服务器收到的请求，主要用于测试或诊断。
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:46
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TRACE {
}
