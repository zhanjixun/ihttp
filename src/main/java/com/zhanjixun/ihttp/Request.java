package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.Param;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
	private List<Header> headers;
	//请求参数
	private List<Param> params;
	//文件
	private List<FileParts> fileParts;

	@Override
	public String toString() {
		return getMethod() + " " + url;
	}

}
