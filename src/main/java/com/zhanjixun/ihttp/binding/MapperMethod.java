package com.zhanjixun.ihttp.binding;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.File;
import java.util.Map;

/**
 * @author zhanjixun
 */
@Data
public class MapperMethod {

    private String name;
    private String url;
    private String method;
    private String requestCharset;
    private String responseCharset;
    private String body;
    private boolean followRedirects;
    
    private Map<String, String> headers = Maps.newHashMap();
    private Map<String, String> params = Maps.newHashMap();
    private Map<String, File> files = Maps.newHashMap();

    private ParamMapping paramMapping;

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
