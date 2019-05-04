package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为一个Mapper接口添加网络代理
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
    /**
     * 代理主机
     *
     * @return
     */
    String hostName();

    /**
     * 代理服务器监听端口
     *
     * @return
     */
    int port();

    /**
     * 是否信任https的SSL证书,不信任可能会抛异常
     *
     * @return
     */
    boolean trustSSL() default true;
}
