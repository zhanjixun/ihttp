package com.zhanjixun.ihttp.binding;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.Request;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zhanjixun.ihttp.parsing.AnnotationParser.HEADER_ANNOTATIONS;

/**
 * 对应一个定义的Mapper接口
 *
 * @author zhanjixun
 */
@Data
public class Mapper {
    @Getter
    private Configuration configuration = Configuration.getDefault();
    //Mapper定义类
    private final Class<?> mapperInterface;
    //存放所有方法
    private final Map<String, MapperMethod> methods = Maps.newHashMap();

    private String commonUrl;
    private List<NameValuePair> commonHeaders = Lists.newArrayList();

    public Mapper(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Request getRequest(String id, Object... args) {
        MapperMethod mapperMethod = methods.get(id);

        Request request = new Request();
        request.setId(mapperInterface.getName() + "#" + id);
        //来自于类上面的注解配置
        request.getHeaders().addAll(commonHeaders);
        request.setUrl(commonUrl);
        request.setCharset(mapperMethod.getCharset());
        request.setResponseCharset(mapperMethod.getResponseCharset());

        //来自于方法上面的注解配置
        request.setUrl(buildUrl(request.getUrl(), mapperMethod.getUrl()));
        request.setMethod(mapperMethod.getMethod());
        request.setFollowRedirects(mapperMethod.isFollowRedirects());

        request.setBody(mapperMethod.getStringBody());

        request.getHeaders().addAll(mapperMethod.getHeaders());
        request.getParams().addAll(mapperMethod.getParams());
        request.getFileParts().addAll(mapperMethod.getFileParts());

        //绑定实时生成的内容
        bingGenerate(request, mapperMethod.getGenerate());
        //绑定动态参数
        bindingParameter(request, mapperMethod.getParamMapping(), args);
        //替换占位符
        replacePlaceholder(request, mapperMethod.getParamMapping(), args);

        return request;
    }

    private void bingGenerate(Request request, Annotation[] generate) {
        //随机码占位符
        Map<String, String> replacementMap = Maps.newHashMap();
        for (Annotation annotation : generate) {
            //随机参数
            if (annotation.annotationType() == RandomParam.class) {
                RandomParam randomParam = (RandomParam) annotation;
                String value = RandomStringUtils.random(randomParam.length(), randomParam.chars());
                value = randomParam.encode() ? StrUtils.URLEncoder(value, request.getCharset()) : value;

                request.getParams().add(new NameValuePair(randomParam.name(), value));
            }
            //时间戳参数
            if (annotation.annotationType() == TimestampParam.class) {
                TimestampParam timestampParam = (TimestampParam) annotation;
                String value = timestampParam.unit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";

                request.getParams().add(new NameValuePair(timestampParam.name(), value));
            }
            //随机占位符
            if (annotation.annotationType() == RandomPlaceholder.class) {
                RandomPlaceholder randomPlaceholder = (RandomPlaceholder) annotation;
                String target = String.format("#{%s}", randomPlaceholder.name());
                String value = RandomStringUtils.random(randomPlaceholder.length(), randomPlaceholder.chars());
                replacementMap.put(target, value);
            }
            //时间戳占位符
            if (annotation.annotationType() == TimestampPlaceholder.class) {
                TimestampPlaceholder timestampPlaceholder = (TimestampPlaceholder) annotation;
                String target = String.format("#{%s}", timestampPlaceholder.name());
                String value = timestampPlaceholder.unit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
                replacementMap.put(target, value);
            }
        }
        replace(request, replacementMap);
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
        Map<String, String> replacementMap = Maps.newHashMap();
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
                throw new NullPointerException(target + "的替换值为null");
            }
            if (((Placeholder) annotation).encode()) {
                value = StrUtils.URLEncoder(value, request.getCharset());
            }
            replacementMap.put(target, value);
        }
        replace(request, replacementMap);
    }

    /**
     * 绑定动态参数
     *
     * @param request
     * @param parameterMapping
     * @param args
     */
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
                Param paramAnnotation = (Param) annotation;
                String value = arg.toString();
                if (paramAnnotation.encode()) {
                    value = StrUtils.URLEncoder(value, request.getCharset());
                }
                request.getParams().add(new NameValuePair(paramAnnotation.name(), value));
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

    private void replace(Request request, Map<String, String> replacementMap) {
        replacementMap.forEach((target, replacement) -> {
            //替换URL
            request.setUrl(StringUtils.replace(request.getUrl(), target, replacement));
            //替换Body
            request.setBody(StringUtils.replace(request.getBody(), target, replacement));
            //替换请求头
            for (NameValuePair nameValuePair : request.getHeaders()) {
                nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, replacement));
            }
            //替换请求参数
            for (NameValuePair nameValuePair : request.getParams()) {
                nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, replacement));
            }
        });
    }

    private String buildUrl(String a, String b) {
        a = StringUtils.isEmpty(a) ? "" : a;
        b = StringUtils.isEmpty(b) ? "" : b;

        return b.startsWith("http") ? b : a + b;
    }
}

