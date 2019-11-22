package com.zhanjixun.ihttp.binding;

import lombok.Data;

import java.util.List;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 11:54
 * @contact :zhanjixun@qq.com
 */
@Data
public class MapperParameter {

	private final String name;

	private final int index;

	//注解属性

	private boolean isURLAnnotated;

	private List<EncodableString> requestParamNames;

	private List<EncodableString> requestHeaderNames;

	private List<String> requestMultiPartNames;

	private EncodableString requestBodyName;

	private EncodableString placeholder;

}
