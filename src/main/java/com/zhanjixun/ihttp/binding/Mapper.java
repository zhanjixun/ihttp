package com.zhanjixun.ihttp.binding;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.constant.Config;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import static com.zhanjixun.ihttp.parsing.AnnotationParser.HEADER_ANNOTATIONS;

/**
 * 对应一个定义的Mapper接口
 *
 * @author zhanjixun
 */
@Data
public class Mapper {

    //Mapper定义类
    private Class<?> mapperInterface;
    //存放所有方法
    private final Map<String, MapperMethod> methods = Maps.newHashMap();
    //缓存已经使用过的方法
    private final Map<String, Request> requestCache = Maps.newHashMap();

    private Config config;
    private String commonUrl;
    private String commonRequestCharset;
    private String commonResponseCharset;
    private Map<String, String> commonHeaders = Maps.newHashMap();

    public Mapper(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Request getRequest(String id, Object... args) {
        Request request = cacheRequest(id);
        bindingParameter(request, args);
        return request;
    }

    public void addMethod(String name, MapperMethod mapperMethod) {
        methods.put(name, mapperMethod);
    }
    
    private Request cacheRequest(String id) {
        Request request = requestCache.get(id);
        if (request != null) {
            return request;
        }
        MapperMethod mapperMethod = methods.get(id);
        Preconditions.checkArgument(Objects.nonNull(mapperMethod), String.format("没有找到id为%s的HTTP请求", id));

        request = new Request();
        request.setId(id);
        //来自于类上面的注解配置
        setCommonAnnotation(request);

        //来自于方法上面的注解配置
        request.setUrl(buildUrl(request.getUrl(), mapperMethod.getUrl()));
        request.setMethod(mapperMethod.getMethod());
        request.setFollowRedirects(mapperMethod.isFollowRedirects());

        request.setBody(mapperMethod.getBody());

        if (StringUtils.isNotEmpty(mapperMethod.getRequestCharset())) {
            request.setCharset(mapperMethod.getRequestCharset());
        }
        if (StringUtils.isNotEmpty(mapperMethod.getResponseCharset())) {
            request.setResponseCharset(mapperMethod.getResponseCharset());
        }
        request.getHeaders().putAll(mapperMethod.getHeaders());
        request.getParams().putAll(mapperMethod.getParams());
        request.getFiles().putAll(mapperMethod.getFiles());
        request.setParameterMapping(mapperMethod.getParamMapping());
        return request;
    }

    private void setCommonAnnotation(Request request) {
        request.getHeaders().putAll(commonHeaders);
        request.setCharset(commonRequestCharset);
        request.setResponseCharset(commonResponseCharset);
        request.setUrl(commonUrl);
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
                request.setUrl(buildUrl(request.getUrl(), url));
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
                    if (annotationType != entry.getValue()) {
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

    private String buildUrl(String a, String b) {
        a = StringUtils.isEmpty(a) ? "" : a;
        b = StringUtils.isEmpty(b) ? "" : b;

        return b.startsWith("http") ? b : a + b;
    }
}

