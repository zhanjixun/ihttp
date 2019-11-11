package com.zhanjixun.ihttp.domain;

import com.zhanjixun.ihttp.executor.ComponentsHttpClientExecutor;
import com.zhanjixun.ihttp.executor.Executor;
import lombok.Builder;
import lombok.Data;

/**
 * 一些配置类
 *
 * @author :zhanjixun
 * @date : 2018/12/3 15:05
 */
@Data
@Builder
public class Configuration {

	private HttpProxy proxy;

	private boolean cookieEnable = true;

	private Class<? extends Executor> executor;

	public static Configuration getDefault() {
		return builder().proxy(null).cookieEnable(true)
				.executor(ComponentsHttpClientExecutor.class)
				.build();
	}
}
