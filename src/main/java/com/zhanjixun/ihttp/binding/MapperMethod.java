package com.zhanjixun.ihttp.binding;

import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;

import java.lang.annotation.Annotation;
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
    private String requestCharset;
    private String responseCharset;
    private String stringBody;
    private boolean followRedirects = true;

    private List<NameValuePair> headers = Lists.newArrayList();
    private List<NameValuePair> params = Lists.newArrayList();
    private List<FileParts> fileParts = Lists.newArrayList();

    private Annotation[] paramMapping;
}
