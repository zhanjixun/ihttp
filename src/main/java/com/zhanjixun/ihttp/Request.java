package com.zhanjixun.ihttp;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.binding.ParamMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.Map;

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

    private Map<String, String> headers = Maps.newHashMap();
    private Map<String, String> params = Maps.newHashMap();
    private Map<String, File> files = Maps.newHashMap();

    private ParamMapping parameterMapping;


    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public void addFile(String name, File file) {
        files.put(name, file);
    }

    public File getFile(String name) {
        return files.get(name);
    }

}
