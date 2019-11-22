package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.RetryPolicy;
import lombok.Data;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 11:45
 * @contact :zhanjixun@qq.com
 */
@Data
public class RetryableFunction {

	private Class<? extends Throwable> throwable;

	private Class<? extends RetryPolicy>[] policy;

	private int maxAttempts;

	private long delay;

	private long multiplier;

}
