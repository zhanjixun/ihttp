package com.zhanjixun.ihttp.binding;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

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
    private String body;
    private boolean followRedirects = true;

    private Map<String, String> headers = Maps.newHashMap();
    private Map<String, String> params = Maps.newHashMap();
    private Map<String, File> files = Maps.newHashMap();

    private Annotation[] paramMapping;
}
