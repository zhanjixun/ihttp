package com.zhanjixun.ihttp;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
@Data
public class Request implements Serializable {

	private static final long serialVersionUID = 3720684088323984812L;

	private String name;
	//请求地址
	private String url;
	//请求编码
	private String method;
	//跟随重定向
	private Boolean followRedirects;
	//请求体
	private String body;
	//请求字符编码
	private String charset;
	//指定响应字符编码
	private String responseCharset;

	//请求头
	private Map<String, String> headers;
	//请求参数
	private Map<String, String> params;
	//文件
	private Map<String, File> fileParts;

	@Override
	public String toString() {
		return getMethod() + " " + url;
	}

}
