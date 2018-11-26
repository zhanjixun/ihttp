package com.zhanjixun.ihttp.parsing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.domain.NameValuePair;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
        HTTP_METHOD_ANNOTATIONS.add(DELETE.class);
        HTTP_METHOD_ANNOTATIONS.add(HEAD.class);
        HTTP_METHOD_ANNOTATIONS.add(PUT.class);

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
        parseMethodAnnotation(mapper);
        return mapper;
    }

    private void parseClassAnnotation(Mapper mapper) {
        for (Annotation annotation : target.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == URL.class) {
                mapper.setCommonUrl(((URL) annotation).value());
            }
            if (annotationType == RequestCharset.class) {
                mapper.setCommonRequestCharset(((RequestCharset) annotation).value());
            }
            if (annotationType == ResponseCharset.class) {
                mapper.setCommonResponseCharset(((ResponseCharset) annotation).value());
            }
        }
        //解析请求头
        mapper.getCommonHeaders().addAll(parseHeader(target));
    }

    private void parseMethodAnnotation(Mapper mapper) {
        for (Method method : target.getDeclaredMethods()) {
            MapperMethod mapperMethod = new MapperMethod();
            mapperMethod.setName(method.getName());
            //URL
            if (Objects.nonNull(method.getAnnotation(URL.class))) {
                mapperMethod.setUrl(method.getAnnotation(URL.class).value());
            }

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
                mapperMethod.setRequestCharset(((GET) httpMethod.get(0)).charset());
            }
            if (annotation instanceof POST) {
                mapperMethod.setRequestCharset(((POST) httpMethod.get(0)).charset());
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
            if (method.getAnnotation(Param.class) != null) {
                Param p = method.getAnnotation(Param.class);
                mapperMethod.getParams().add(new NameValuePair(p.name(), p.value()));
            }
            if (method.getAnnotation(Params.class) != null) {
                for (Param p : method.getAnnotation(Params.class).value()) {
                    mapperMethod.getParams().add(new NameValuePair(p.name(), p.value()));
                }
            }

            if (method.getAnnotation(FilePart.class) != null) {
                FilePart f = method.getAnnotation(FilePart.class);
                mapperMethod.getMultiParts().add(new com.zhanjixun.ihttp.domain.MultiParts(f.name(), new File(f.value())));
            }
            if (method.getAnnotation(MultiParts.class) != null) {
                for (FilePart f : method.getAnnotation(MultiParts.class).value()) {
                    mapperMethod.getMultiParts().add(new com.zhanjixun.ihttp.domain.MultiParts(f.name(), new File(f.value())));
                }
            }

            mapperMethod.setRequestCharset(method.getAnnotation(RequestCharset.class) == null ? null : method.getAnnotation(RequestCharset.class).value());
            mapperMethod.setResponseCharset(method.getAnnotation(ResponseCharset.class) == null ? null : method.getAnnotation(ResponseCharset.class).value());

            //直接请求体
            mapperMethod.setStringBody(method.getAnnotation(StringBody.class) == null ? null : method.getAnnotation(StringBody.class).value());

            //随机码占位符
            Map<String, String> replacement = Maps.newHashMap();
            RandomPlaceholder randomPlaceholder = method.getAnnotation(RandomPlaceholder.class);
            if (randomPlaceholder != null) {
                String target = String.format("#{%s}", randomPlaceholder.name());
                String value = RandomStringUtils.random(randomPlaceholder.length(), randomPlaceholder.chars());
                replacement.put(target, value);
            }
            //时间戳占位符
            TimestampPlaceholder timestampPlaceholder = method.getAnnotation(TimestampPlaceholder.class);
            if (timestampPlaceholder != null) {
                String target = String.format("#{%s}", timestampPlaceholder.name());
                String value = timestampPlaceholder.unit().convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) + "";
                replacement.put(target, value);
            }
            if (MapUtils.isNotEmpty(replacement)) {
                replacement.forEach((target, value) -> {
                    mapperMethod.setUrl(StringUtils.replace(mapperMethod.getUrl(), target, value));
                    mapperMethod.setStringBody(StringUtils.replace(mapperMethod.getStringBody(), target, value));
                    for (NameValuePair nameValuePair : mapperMethod.getHeaders()) {
                        nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, value));
                    }
                    for (NameValuePair nameValuePair : mapperMethod.getParams()) {
                        nameValuePair.setValue(StringUtils.replace(nameValuePair.getValue(), target, value));
                    }
                });
            }

            mapper.addMethod(method.getName(), mapperMethod);
        }
    }

    private List<NameValuePair> parseHeader(AnnotatedElement element) {
        List<NameValuePair> headers = Lists.newArrayList();

        if (element.getAnnotation(Header.class) != null) {
            Header header = element.getAnnotation(Header.class);
            headers.add(new NameValuePair(header.name(), header.value()));
        }
        if (element.getAnnotation(Headers.class) != null) {
            Arrays.stream(element.getAnnotation(Headers.class).value()).forEach(header ->
                    headers.add(new NameValuePair(header.name(), header.value())));
        }
        for (Map.Entry<String, Class<? extends Annotation>> entry : HEADER_ANNOTATIONS.entrySet()) {
            Class<? extends Annotation> annotationClass = entry.getValue();
            if (element.isAnnotationPresent(annotationClass)) {
                Annotation annotation = element.getAnnotation(annotationClass);
                try {
                    String value = (String) annotationClass.getMethod("value").invoke(annotation);
                    headers.add(new NameValuePair(entry.getKey(), value));
                } catch (Exception e) {
                    throw new RuntimeException("Could not find value method on Header annotation.  Cause: " + e, e);
                }
            }
        }
        return headers;
    }
}
