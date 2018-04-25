package com.zhanjixun.ihttp;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.binding.ParamMapping;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Map;

/**
 * http请求实体
 *
 * @author zhanjixun
 */
@EqualsAndHashCode
public class Request {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String body;
    @Getter
    @Setter
    private String charset;
    @Getter
    @Setter
    private String responseCharset;
    @Getter
    private Map<String, String> headers = Maps.newHashMap();
    @Getter
    private Map<String, String> params = Maps.newHashMap();
    @Getter
    private Map<String, File> files = Maps.newHashMap();
    @Getter
    @Setter
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
