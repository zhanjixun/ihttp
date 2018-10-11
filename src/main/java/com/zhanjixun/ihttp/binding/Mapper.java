package com.zhanjixun.ihttp.binding;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.constant.Config;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
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

    private Config config;
    private String commonUrl;
    private String commonRequestCharset;
    private String commonResponseCharset;
    private Map<String, String> commonHeaders = Maps.newHashMap();

    public Mapper(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Request getRequest(String id, Object... args) {
        MapperMethod mapperMethod = methods.get(id);
        Preconditions.checkArgument(Objects.nonNull(mapperMethod), String.format("没有找到id为%s.%s的HTTP请求", mapperInterface.getName(), id));

        Request request = new Request();
        request.setId(mapperInterface.getName() + "." + id);
        //来自于类上面的注解配置
        request.getHeaders().putAll(commonHeaders);
        request.setCharset(commonRequestCharset);
        request.setResponseCharset(commonResponseCharset);
        request.setUrl(commonUrl);

        //来自于方法上面的注解配置
        request.setUrl(buildUrl(request.getUrl(), mapperMethod.getUrl()));
        request.setMethod(mapperMethod.getMethod());
        request.setFollowRedirects(mapperMethod.isFollowRedirects());

        request.setBody(mapperMethod.getBody());

        request.setCharset(chooseCharset(request.getCharset(), mapperMethod.getRequestCharset()));
        request.setResponseCharset(chooseCharset(request.getResponseCharset(), mapperMethod.getResponseCharset()));

        request.getHeaders().putAll(mapperMethod.getHeaders());
        request.getParams().putAll(mapperMethod.getParams());
        request.getFiles().putAll(mapperMethod.getFiles());

        //绑定动态参数
        bindingParameter(request, mapperMethod.getParamMapping(), args);
        Preconditions.checkArgument(StringUtils.isNotBlank(request.getUrl()), String.format("HTTP请求没有设置url %s.%s", mapperInterface.getName(), id));

        return request;
    }

    public void addMethod(String name, MapperMethod mapperMethod) {
        methods.put(name, mapperMethod);
    }

    private void bindingParameter(Request request, Annotation[] parameterMapping, Object... args) {
        if (ArrayUtils.isEmpty(parameterMapping)) {
            return;
        }
        for (int i = 0; i < parameterMapping.length; i++) {
            Annotation annotation = parameterMapping[i];
            Class<? extends Annotation> annotationType = annotation.annotationType();

            Object arg = args[i];
            if (annotationType == URL.class) {
                String url = (String) arg;
                request.setUrl(buildUrl(request.getUrl(), url));
            }
            if (annotationType == Header.class) {
                Header header = (Header) annotation;
                request.addHeader(header.name(), (String) arg);
            }
            if (annotationType == Param.class) {
                Param param = (Param) annotation;
                request.addParam(param.name(), (String) arg);
            }
            if (annotationType == FilePart.class) {
                FilePart filePart = (FilePart) annotation;
                if (arg instanceof String) {
                    request.addFile(filePart.name(), new File((String) arg));
                } else if (arg instanceof File) {
                    request.addFile(filePart.name(), (File) arg);
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + FilePart.class.getName() + "时，被注解的参数类型必须为java.lang.String或者java.io.File");
                }
            }
            if (annotationType == StringBody.class) {
                request.setBody((String) arg);
            }
            if (annotationType == ParamMap.class) {
                if (arg instanceof Map) {
                    ((Map<String, ? extends Object>) arg).forEach((k, v) -> request.addParam(k, String.valueOf(v)));
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + ParamMap.class.getName() + "时，被注解的参数类型必须为java.util.Map");
                }
            }
            HEADER_ANNOTATIONS.entrySet().stream().filter(entry -> annotationType == entry.getValue()).forEach(entry -> request.addHeader(entry.getKey(), (String) arg));
        }
    }

    private String chooseCharset(String a, String b) {
        if (StringUtils.isNotBlank(b)) {
            return b;
        }
        if (StringUtils.isNotBlank(a)) {
            return a;
        }
        return null;
    }

    private String buildUrl(String a, String b) {
        a = StringUtils.isEmpty(a) ? "" : a;
        b = StringUtils.isEmpty(b) ? "" : b;

        return b.startsWith("http") ? b : a + b;
    }
}

