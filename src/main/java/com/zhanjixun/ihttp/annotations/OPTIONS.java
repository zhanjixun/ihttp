package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用OPTIONS请求:
 * 允许客户端查看服务器的性能。
 *
 * @author :zhanjixun
 * @date : 2019/11/23 17:46
 * @contact :zhanjixun@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OPTIONS {
}
