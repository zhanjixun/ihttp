package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 使用Map<String,Object>批量添加参数
 *
 * @author :zhanjixun
 * @date : 2018/8/20 11:05
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamMap {

	/**
	 * 是否进行编码
	 *
	 * @return
	 * @see GET#charset()
	 * @see POST#charset()
	 */
	boolean encode() default false;

}
