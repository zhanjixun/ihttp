package com.zhanjixun.ihttp.parsing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.domain.FileParts;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.domain.NameValuePair;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 解析注解方式Mapper
 *
 * @author zhanjixun
 */
@Log4j
public class AnnotationParser implements Parser {

    private Class<?> target;
    public static final Map<String, Class<? extends Annotation>> HEADER_ANNOTATIONS = Maps.newHashMap();
    public static final List<Class<? extends Annotation>> HTTP_METHOD_ANNOTATIONS = Lists.newArrayList();
    public static final List<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Lists.newArrayList();

    static {
        HEADER_ANNOTATIONS.put("Accept", Accept.class);
        HEADER_ANNOTATIONS.put("Accept-Encoding", AcceptEncoding.class);
        HEADER_ANNOTATIONS.put("Accept-Language", AcceptLanguage.class);
        HEADER_ANNOTATIONS.put("Content-Type", ContentType.class);
        HEADER_ANNOTATIONS.put("Origin", Origin.class);
        HEADER_ANNOTATIONS.put("Referer", Referer.class);
        HEADER_ANNOTATIONS.put("User-Agent", UserAgent.class);

        HTTP_METHOD_ANNOTATIONS.add(GET.class);
        HTTP_METHOD_ANNOTATIONS.add(POST.class);

        PARAMETER_ANNOTATIONS.add(URL.class);
        PARAMETER_ANNOTATIONS.add(Param.class);
        PARAMETER_ANNOTATIONS.add(FilePart.class);
        PARAMETER_ANNOTATIONS.add(StringBody.class);
        PARAMETER_ANNOTATIONS.add(StringBodyObject.class);
        PARAMETER_ANNOTATIONS.add(Header.class);
        PARAMETER_ANNOTATIONS.add(ParamMap.class);
        PARAMETER_ANNOTATIONS.add(Placeholder.class);
        PARAMETER_ANNOTATIONS.addAll(HEADER_ANNOTATIONS.values());
    }

    public AnnotationParser(Class<?> target) {
        this.target = target;
    }

    @Override
    public Mapper parse() {
        Mapper mapper = new Mapper(target);
        parseClassAnnotation(mapper);
        for (Method method : target.getDeclaredMethods()) {
            mapper.addMethod(method.getName(), parseMethodAnnotation(method, mapper));
        }
        return mapper;
    }

    private void parseClassAnnotation(Mapper mapper) {
        ReflectUtils.containsAnnotation(target, Proxy.class, p -> mapper.getConfiguration().setProxy(new HttpProxy(p.hostName(), p.port(), p.trustSSL())));
        ReflectUtils.containsAnnotation(target, HttpExecutor.class, e -> mapper.getConfiguration().setExecutor(e.value()));
        ReflectUtils.containsAnnotation(target, DisableCookie.class, e -> mapper.getConfiguration().setCookieEnable(false));

        ReflectUtils.containsAnnotation(target, URL.class, e -> mapper.setCommonUrl(e.value()));
        //解析请求头
        mapper.getCommonHeaders().addAll(parseHeader(target));
    }

    private MapperMethod parseMethodAnnotation(Method method, Mapper mapper) {
        MapperMethod mapperMethod = new MapperMethod();
        mapperMethod.setName(method.getName());
        //URL
        ReflectUtils.containsAnnotation(method, URL.class, annotation -> mapperMethod.setUrl(annotation.value()));

        //http方法
        List<? extends Annotation> httpMethod = HTTP_METHOD_ANNOTATIONS.stream().map(method::getAnnotation).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(httpMethod)) {
            throw new RuntimeException(String.format("没有找到HTTP请求方法 %s", target.getName() + "." + method.getName()));
        }
        if (httpMethod.size() > 1) {
            throw new RuntimeException(String.format("重复设置HTTP请求方法 %s[%s]", target.getName() + "." + method.getName(), String.join(",", httpMethod.stream().map(d -> d.annotationType().getSimpleName()).collect(Collectors.toList()))));
        }
        Annotation annotation = httpMethod.get(0);
        mapperMethod.setMethod(annotation.annotationType().getSimpleName());
        if (annotation instanceof GET) {
            mapperMethod.setFollowRedirects(((GET) httpMethod.get(0)).followRedirects());
            mapperMethod.setCharset(((GET) httpMethod.get(0)).charset());
        }
        if (annotation instanceof POST) {
            mapperMethod.setCharset(((POST) httpMethod.get(0)).charset());
        }

        //解析方法形参
        Parameter[] parameters = method.getParameters();
        Annotation[] parameterAMapping = new Annotation[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            List<Annotation> annotations = Arrays.stream(parameters[i].getAnnotations())
                    .filter(a -> PARAMETER_ANNOTATIONS.contains(a.annotationType()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(annotations)) {
                continue;
            }
            parameterAMapping[i] = annotations.get(0);
        }
        mapperMethod.setParamMapping(parameterAMapping);

        //请求头
        mapperMethod.getHeaders().addAll(parseHeader(method));

        //固定参数
        for (Param param : ReflectUtils.getRepeatableAnnotation(method, Param.class)) {
            String value = param.encode() ? StrUtils.URLEncoder(param.value(), mapperMethod.getCharset()) : param.value();
            mapperMethod.getParams().add(new NameValuePair(param.name(), value));
        }
        //随机参数
        for (RandomParam randomParam : ReflectUtils.getRepeatableAnnotation(method, RandomParam.class)) {
            String value = RandomStringUtils.random(randomParam.length(), randomParam.chars());
            value = randomParam.encode() ? StrUtils.URLEncoder(value, mapperMethod.getCharset()) : value;

            mapperMethod.getParams().add(new NameValuePair(randomParam.name(), value));
        }
        //时间戳参数
        for (TimestampParam timestampParam : ReflectUtils.getRepeatableAnnotation(method, TimestampParam.class)) {
            String value = timestampParam.unit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
            mapperMethod.getParams().add(new NameValuePair(timestampParam.name(), value));
        }

        //文件上传
        for (FilePart filePart : ReflectUtils.getRepeatableAnnotation(method, FilePart.class)) {
            mapperMethod.getFileParts().add(new FileParts(filePart.name(), new File(filePart.value())));
        }

        //直接请求体
        mapperMethod.setStringBody(method.getAnnotation(StringBody.class) == null ? null : method.getAnnotation(StringBody.class).value());

        //随机码占位符
        Map<String, String> replacementMap = Maps.newHashMap();
        for (RandomPlaceholder randomPlaceholder : ReflectUtils.getRepeatableAnnotation(method, RandomPlaceholder.class)) {
            String target = String.format("#{%s}", randomPlaceholder.name());
            String value = RandomStringUtils.random(randomPlaceholder.length(), randomPlaceholder.chars());
            replacementMap.put(target, value);
        }
        //时间戳占位符
        for (TimestampPlaceholder timestampPlaceholder : ReflectUtils.getRepeatableAnnotation(method, TimestampPlaceholder.class)) {
            String target = String.format("#{%s}", timestampPlaceholder.name());
            String value = timestampPlaceholder.unit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
            replacementMap.put(target, value);
        }
        replacementMap.forEach((target, replacement) -> {
            //替换URL
            mapperMethod.setUrl(StringUtils.replace(mapperMethod.getUrl(), target, replacement));
            //替换StringBody
            mapperMethod.setStringBody(StringUtils.replace(mapperMethod.getStringBody(), target, replacement));
            //替换请求头
            for (NameValuePair nameValuePair : mapperMethod.getHeaders()) {
                nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, replacement));
            }
            //替换请求参数
            for (NameValuePair nameValuePair : mapperMethod.getParams()) {
                nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, replacement));
            }
        });
        return mapperMethod;
    }

    private List<NameValuePair> parseHeader(AnnotatedElement element) {
        List<NameValuePair> headers = Lists.newArrayList();

        for (Header header : ReflectUtils.getRepeatableAnnotation(element, Header.class)) {
            headers.add(new NameValuePair(header.name(), header.value()));
        }

        for (Map.Entry<String, Class<? extends Annotation>> entry : HEADER_ANNOTATIONS.entrySet()) {
            ReflectUtils.containsAnnotation(element, entry.getValue(), annotation -> {
                String value = (String) ReflectUtils.invokeAnnotationMethod(annotation, "value");
                headers.add(new NameValuePair(entry.getKey(), value));
            });
        }
        return headers;
    }
}
