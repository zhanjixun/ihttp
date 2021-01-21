package com.zhanjixun.ihttp.annotations;

import com.alibaba.fastjson.JSON;

import java.lang.annotation.*;

/**
 * 设置请求正文，直接将字符串写入请求体
 * 1.在方法上使用时候，需要指定value作为请求体
 * 2.在参数上使用时候，如果是字符串类型，直接作为请求体，其他类型(如Map,实体类)序列化成json字符串作为请求体
 *
 * @see ContentType#value()
 * @see JSON#toJSONString(Object)
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {

    /**
     * 内容类型
     *
     * @return
     */
    String contentType() default "application/json;charset=UTF-8";

    /**
     * 值
     *
     * @return
     */
    String value() default "";

    /**
     * 是否进行编码
     */
    boolean encode() default false;
}
