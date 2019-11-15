package com.zhanjixun.ihttp.annotations;

import java.lang.annotation.*;

/**
 * 在方法参数中指定一个对象来动态生成json字符串，作为http请求体
 * 可用在POJO类、集合或者Map对象上
 *
 * @author :zhanjixun
 * @date : 2018/10/24 11:25
 * @see com.alibaba.fastjson.JSON#toJSONString(Object)
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringBodyObject {

	/**
	 * 是否进行编码
	 *
	 * @return
	 * @see GET#charset()
	 * @see POST#charset()
	 */
	boolean encode() default false;

}
