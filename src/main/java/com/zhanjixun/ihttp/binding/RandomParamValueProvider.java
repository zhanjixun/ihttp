package com.zhanjixun.ihttp.binding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 11:38
 * @contact :zhanjixun@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomParamValueProvider implements ValueProvider {

	private String name;

	private int length;

	private String chars;

	private boolean encode;

	@Override
	public Object value() {
		return null;
	}

}
