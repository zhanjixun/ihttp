package com.zhanjixun.ihttp.binding;

import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应一个Mapper中定义的方法
 *
 * @author zhanjixun
 */
@Data
public class MapperMethod {

	private String name;
	private String url;
	private String method;
	private String charset;
	private String responseCharset;
	private String stringBody;

	private boolean followRedirects = true;

	private List<NameValuePair> headers = new ArrayList<>();
	private List<NameValuePair> params = new ArrayList<>();
	private List<FileParts> fileParts = new ArrayList<>();

	//自动生成的参数
	private Annotation[] generate;

	//动态参数
	private Annotation[] paramMapping;
}
