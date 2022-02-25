package com.zhanjixun.ihttp.annotations;

import com.zhanjixun.ihttp.domain.MultipartFile;

import java.lang.annotation.*;

/**
 * 上传文件：
 * 1.标记在方法体上 value指定文件地址
 * 2.标记在方法参数上 可以使用
 * 2.1 String        类型 文件地址
 * 2.2 File          类型 文件
 * 2.3 MultipartFile 类型 绑定文件信息 分片上传可用此类型
 *
 * @author zhanjixun
 * @see MultipartFile
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestPart.List.class)
public @interface RequestPart {
    /**
     * @return
     */
    String name();

    /**
     * @return
     */
    String value() default "";

    //指定多个时使用
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        RequestPart[] value();
    }
}
