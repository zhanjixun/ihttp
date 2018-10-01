package com.zhanjixun.ihttp.binding;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.constant.Config;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import static com.zhanjixun.ihttp.parsing.AnnotationParser.HEADER_ANNOTATIONS;

/**
 * @author zhanjixun
 */
public class Mapper {

    private final Map<String, MapperMethod> methods = Maps.newHashMap();
    private final Map<String, Request> requestCache = Maps.newHashMap();
    @Getter
    @Setter
    private Class<?> mapperInterface;
    @Getter
    @Setter
    private Config config;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String requestCharset;
    @Getter
    @Setter
    private String responseCharset;
    @Getter
    private Map<String, String> headers = Maps.newHashMap();

    public Mapper(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Request getRequest(String id, Object... args) {
        Request request = cacheRequest(id);
        bindingParameter(request, args);
        //出厂检验
        Preconditions.checkArgument(StringUtils.isNotEmpty(request.getUrl()), String.format("请求的URL不能为空:%s", mapperInterface.getName() + "." + request.getId()));
        return request;
    }

    private Request cacheRequest(String id) {
        Request request = requestCache.get(id);
        if (request != null) {
            return request;
        }
        MapperMethod mapperMethod = methods.get(id);
        Preconditions.checkArgument(Objects.nonNull(mapperMethod), String.format("没有找到id为%s的HTTP请求。", id));

        request = new Request();
        request.setId(id);
        //来自于类上面的注解配置
        setGlobalAnnotation(request);

        //来自于方法上面的注解配置
        if (StringUtils.isNotEmpty(request.getUrl()) & !StringUtils.startsWith(mapperMethod.getUrl(), "http")) {
            request.setUrl(request.getUrl() + mapperMethod.getUrl());
        } else {
            request.setUrl(mapperMethod.getUrl());
        }
        request.setMethod(mapperMethod.getMethod());
        request.setFollowRedirects(mapperMethod.isFollowRedirects());

        request.setBody(mapperMethod.getBody());
        if (StringUtils.isNotEmpty(mapperMethod.getRequestCharset())) {
            request.setCharset(mapperMethod.getRequestCharset());
        }
        if (StringUtils.isNotEmpty(mapperMethod.getResponseCharset())) {
            request.setResponseCharset(mapperMethod.getResponseCharset());
        }
        request.setParameterMapping(mapperMethod.getParamMapping());
        for (Map.Entry<String, String> entry : mapperMethod.getHeaders().entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : mapperMethod.getParams().entrySet()) {
            request.addParam(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, File> entry : mapperMethod.getFiles().entrySet()) {
            request.addFile(entry.getKey(), entry.getValue());
        }
        return request;
    }

    private void setGlobalAnnotation(Request request) {
        headers.forEach(request::addHeader);
        request.setCharset(requestCharset);
        request.setResponseCharset(responseCharset);
        request.setUrl(url);
    }

    private void bindingParameter(Request request, Object... args) {
        ParamMapping parameterMapping = request.getParameterMapping();
        if (parameterMapping == null) {
            return;
        }
        for (int i = 0; i < parameterMapping.paramTypes().length; i++) {
            Annotation annotation = parameterMapping.paramTypes()[i];
            Class<? extends Annotation> annotationType = annotation.annotationType();

            Object arg = args[i];
            if (annotationType == URL.class) {
                String url = (String) arg;
                if (StringUtils.isNotEmpty(request.getUrl()) && !url.startsWith("http")) {
                    request.setUrl(request.getUrl() + url);
                } else {
                    request.setUrl(url);
                }
            } else if (annotationType == Header.class) {
                Header header = (Header) annotation;
                request.addHeader(header.name(), (String) arg);
            } else if (annotationType == Param.class) {
                Param param = (Param) annotation;
                request.addParam(param.name(), (String) arg);
            } else if (annotationType == FilePart.class) {
                FilePart filePart = (FilePart) annotation;
                File file;
                if (arg instanceof String) {
                    file = new File((String) arg);
                } else if (arg instanceof File) {
                    file = (File) arg;
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + FilePart.class.getName() + "时，被注解的参数类型必须为java.lang.String或者java.io.File");
                }
                request.addFile(filePart.name(), file);
            } else if (annotationType == StringBody.class) {
                request.setBody((String) arg);
            } else if (annotationType == ParamMap.class) {
                if (arg instanceof Map) {
                    ((Map<String, ? extends Object>) arg).forEach((k, v) -> request.addParam(k, String.valueOf(v)));
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + ParamMap.class.getName() + "时，被注解的参数类型必须为java.util.Map");
                }
            } else {
                for (Map.Entry<String, Class<? extends Annotation>> entry : HEADER_ANNOTATIONS.entrySet()) {
                    if (annotationType == entry.getValue()) {
                        try {
                            String value = (String) entry.getValue().getDeclaredMethod("value").invoke(annotation);
                            request.addHeader(entry.getKey(), value);
                        } catch (Exception e) {
                            throw new RuntimeException("Could not find value method on Header annotation.  Cause: " + e, e);
                        }
                    }
                }
            }
        }
    }

    public void addMethod(String name, MapperMethod mapperMethod) {
        methods.put(name, mapperMethod);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}

