package com.zhanjixun.ihttp.binding;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
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

    private String commonUrl;
    private String commonRequestCharset;
    private String commonResponseCharset;
    private List<NameValuePair> commonHeaders = Lists.newArrayList();

    public Mapper(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Request getRequest(String id, Object... args) {
        MapperMethod mapperMethod = methods.get(id);
        Preconditions.checkArgument(Objects.nonNull(mapperMethod), String.format("没有找到id为%s.%s的HTTP请求", mapperInterface.getName(), id));

        Request request = new Request();
        request.setId(mapperInterface.getName() + "." + id);
        //来自于类上面的注解配置
        request.getHeaders().addAll(commonHeaders);
        request.setCharset(commonRequestCharset);
        request.setResponseCharset(commonResponseCharset);
        request.setUrl(commonUrl);

        //来自于方法上面的注解配置
        request.setUrl(buildUrl(request.getUrl(), mapperMethod.getUrl()));
        request.setMethod(mapperMethod.getMethod());
        request.setFollowRedirects(mapperMethod.isFollowRedirects());

        request.setBody(mapperMethod.getStringBody());

        request.setCharset(chooseCharset(request.getCharset(), mapperMethod.getRequestCharset()));
        request.setResponseCharset(chooseCharset(request.getResponseCharset(), mapperMethod.getResponseCharset()));

        request.getHeaders().addAll(mapperMethod.getHeaders());
        request.getParams().addAll(mapperMethod.getParams());
        request.getFileParts().addAll(mapperMethod.getFileParts());

        //绑定动态参数
        bindingParameter(request, mapperMethod.getParamMapping(), args);
        //替换占位符
        replacePlaceholder(request, mapperMethod.getParamMapping(), args);

        Preconditions.checkArgument(StringUtils.isNotBlank(request.getUrl()), String.format("HTTP请求没有设置url %s.%s", mapperInterface.getName(), id));
        return request;
    }

    public void addMethod(String name, MapperMethod mapperMethod) {
        methods.put(name, mapperMethod);
    }

    /**
     * 替换占位符
     *
     * @param request
     * @param parameterMapping
     * @param args
     */
    private void replacePlaceholder(Request request, Annotation[] parameterMapping, Object... args) {
        if (Arrays.stream(parameterMapping).noneMatch(d -> d.annotationType() == Placeholder.class)) {
            return;
        }

        for (int i = 0; i < parameterMapping.length; i++) {
            Annotation annotation = parameterMapping[i];
            Class<? extends Annotation> annotationType = annotation.annotationType();
            Object arg = args[i];

            if (annotationType != Placeholder.class) {
                continue;
            }
            String placeholder = ((Placeholder) annotation).value();
            Preconditions.checkArgument(StringUtils.isNotBlank(placeholder), String.format("占位符为空 %s 参数索引 %d", request.getId(), i));

            String target = String.format("#{%s}", placeholder);
            String value = null;
            if (ReflectUtils.isPrimitive(arg)) {
                value = arg.toString();
            }
            if (arg instanceof String) {
                value = (String) arg;
            }
            if (value == null) {
                throw new RuntimeException(target + "的替换值为null");
            }
            if (((Placeholder) annotation).encode()) {
                try {
                    value = URLEncoder.encode(value, ((Placeholder) annotation).charset());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("占位符URL编码不支持的字符类型:", e);
                }
            }
            //搜索URL
            request.setUrl(request.getUrl().replace(target, value));
            //搜索Body
            request.setBody(StringUtils.replace(request.getBody(), target, value));
            //搜索Header
            for (NameValuePair nameValuePair : request.getHeaders()) {
                nameValuePair.setValue(nameValuePair.getValue().replace(target, value));
            }
            //搜索Params
            for (NameValuePair nameValuePair : request.getParams()) {
                nameValuePair.setValue(nameValuePair.getValue().replace(target, value));
            }
        }
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
                request.setUrl(buildUrl(request.getUrl(), (String) arg));
            }
            if (annotationType == Header.class) {
                request.getHeaders().add(new NameValuePair(((Header) annotation).name(), (String) arg));
            }
            if (annotationType == Param.class) {
                Param paramAnno = (Param) annotation;
                String value = (String) arg;
                if (paramAnno.encode()) {
                    try {
                        value = URLEncoder.encode(value, paramAnno.charset());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("动态请求参数URL编码不支持的字符类型:", e);
                    }
                }
                request.getParams().add(new NameValuePair(paramAnno.name(), value));
            }
            if (annotationType == FilePart.class) {
                FilePart filePart = (FilePart) annotation;
                if (arg instanceof String) {
                    request.getFileParts().add(new FileParts(filePart.name(), new File((String) arg)));
                } else if (arg instanceof File) {
                    request.getFileParts().add(new FileParts(filePart.name(), (File) arg));
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + FilePart.class.getName() + "时，被注解的参数类型必须为java.lang.String或者java.io.File");
                }
            }
            if (annotationType == StringBody.class) {
                request.setBody((String) arg);
            }
            if (annotationType == StringBodyObject.class) {
                request.setBody(JSON.toJSONString(arg));
            }
            if (annotationType == ParamMap.class) {
                if (arg instanceof Map) {
                    ((Map<String, ? extends Object>) arg).forEach((k, v) -> request.getParams().add(new NameValuePair(k, String.valueOf(v))));
                } else {
                    throw new IllegalArgumentException("在方法的参数中使用" + ParamMap.class.getName() + "时，被注解的参数类型必须为java.util.Map");
                }
            }
            HEADER_ANNOTATIONS.entrySet().stream().filter(entry -> annotationType == entry.getValue()).forEach(entry -> request.getHeaders().add(new NameValuePair(entry.getKey(), (String) arg)));
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

