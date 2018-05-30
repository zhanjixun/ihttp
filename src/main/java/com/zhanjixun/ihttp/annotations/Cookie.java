package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Cookies.class)
public @interface Cookie {
    /**
     * 是否使用安全传输协议。为true时，只有当是https请求连接时cookie才会发送给服务器端，而http时不会，但是服务端还是可以发送给浏览端的。
     *
     * @return
     */
    boolean secure() default false;

    /**
     * 符合该pattern（域名正则）的就可以访问该Cookie的域名。如果设置为“.google.com”，则所有以“google.com”结尾的域名都可以访问该Cookie。注意第一个字符必须为“.”
     *
     * @return
     */
    String domain();

    String name();

    /**
     * 给当前cookie赋值
     *
     * @return
     */
    String value();

    /**
     * 设置Cookie的使用路径。后面紧接着详解。如果设置为“/agx/”，则只有uri为“/agx”的程序可以访问该Cookie。如果设置为“/”，则本域名下的uri都可以访问该Cookie。注意最后一个字符必须为”/”
     *
     * @return
     */
    String path() default "/";

    /**
     * 对该cookie进行描述的信息(说明作用)，浏览器显示cookie信息时能看到
     *
     * @return
     */
    String comment() default "";
}
