package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 为HTTP请求带上请求参数
 * 1.可标记在方法体上
 * 2.标记在形式参数上时 参数类型可为
 * 2.1 String 类型 按 name=value 添加http参数
 * 2.2 Map<String,Object> 类型 遍历取name.key=value(map的value) name为空则没有.
 * 2.3 实体类   类型  遍历取所有字段 name.field=value(字段的值) name为空则没有,需要给字段取别名的用fastJson的@JSONField
 *
 * @author zhanjixun
 * @see RandomRequestParam
 * @see TimestampRequestParam
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestParam.List.class)
public @interface RequestParam {
    /**
     * @return
     */
    String name() default "";

    /**
     * @return
     */
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
        RequestParam[] value();
    }
}
