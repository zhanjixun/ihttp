package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 为HTTP请求带上请求参数
 * 1.可标记在方法体上
 * 2.标记在形式参数上时 参数类型可为
 * 2.1 String 类型 按 name=value 添加http参数
 * 2.2 Map<String,Object> 类型 遍历取name.key=value(map的value) name为空则没有.
 * 2.3 实体类   类型  遍历取所有字段 name.field=value(字段的值) name为空则没有.
 *
 * @author zhanjixun
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Param.List.class)
public @interface Param {

    String name() default "";

    String value() default "";

    /**
     * 是否进行编码
     *
     * @return
     * @see GET#charset()
     * @see POST#charset()
     */
    boolean encode() default false;

    //指定多个时使用
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Param[] value();
    }
}
