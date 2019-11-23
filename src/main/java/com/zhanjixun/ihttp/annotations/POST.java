package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 标识一个方法使用POST请求:
 * 向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。
 * 数据被包含在请求体中。
 * POST 请求可能会导致新的资源的建立和/或已有资源的修改。
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface POST {

	/**
	 * 定义用于编码内容体的字符集
	 *
	 * @return
	 */
	String charset() default "UTF-8";

}
