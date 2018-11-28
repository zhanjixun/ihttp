package com.zhanjixun.ihttp;

import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
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
    private String body;
    private String charset;
    private String responseCharset;
    private boolean followRedirects = true;

    private List<NameValuePair> headers = Lists.newArrayList();
    private List<NameValuePair> params = Lists.newArrayList();
    private List<FileParts> fileParts = Lists.newArrayList();

    public void addHeader(String name, String value) {
        headers.add(new NameValuePair(name, value));
    }

    public void addParam(String name, String value) {
        params.add(new NameValuePair(name, value));
    }

    public void addFile(String name, File file) {
        fileParts.add(new FileParts(name, file));
    }

}
