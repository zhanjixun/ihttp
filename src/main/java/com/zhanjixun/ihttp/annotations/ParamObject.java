package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 使用实体类批量添加参数
 * 字段名=字段值
 *
 * @author :zhanjixun
 * @date : 2019/5/4 22:27
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamObject {
}
