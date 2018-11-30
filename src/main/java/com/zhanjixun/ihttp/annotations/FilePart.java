package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注解在参数上 参数类型必须为File或者String
 *
 * @author zhanjixun
 * @see com.zhanjixun.ihttp.domain.ByteArrayFile
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FilePartRepeatable.class)
public @interface FilePart {

    String name();

    String value() default "";
}
