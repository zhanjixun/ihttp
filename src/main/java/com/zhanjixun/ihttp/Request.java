package com.zhanjixun.ihttp;

import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
@EqualsAndHashCode
@Data
public class Request {

    private String id;

    private String url;
    private String method;
    private boolean followRedirects = true;

    private String body;
    private String charset;

    private List<NameValuePair> headers = Lists.newArrayList();
    private List<NameValuePair> params = Lists.newArrayList();
    private List<FileParts> fileParts = Lists.newArrayList();

    @Override
    public String toString() {
        return getMethod() + " " + url;
    }
}
