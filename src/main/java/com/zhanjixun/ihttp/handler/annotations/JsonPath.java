package com.zhanjixun.ihttp.handler.annotations;

import java.lang.annotation.*;

/**
 * 解析返回值的时候用于指定json解析路径
 *
 * @author zhanjixun
 * @date 2020-06-10 14:34:36
 * @see com.alibaba.fastjson.JSONPath
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPath {
    /**
     * 路径
     *
     * @return
     */
    String path();

}
