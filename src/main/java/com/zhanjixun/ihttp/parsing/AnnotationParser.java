package com.zhanjixun.ihttp.parsing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.binding.ParamMapping;
import com.zhanjixun.ihttp.constant.Config;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhanjixun
 */
@Log4j
public class AnnotationParser implements Parser {

    private Class<?> target;
    public static final Map<String, Class<? extends Annotation>> HEADER_ANNOTATIONS = Maps.newHashMap();
    public static final Map<String, Class<? extends Annotation>> HTTP_METHOD_ANNOTATIONS = Maps.newHashMap();
    public static final List<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Lists.newArrayList();

    static {
        HEADER_ANNOTATIONS.put("Accept", Accept.class);
        HEADER_ANNOTATIONS.put("Accept-Encoding", AcceptEncoding.class);
        HEADER_ANNOTATIONS.put("Accept-Language", AcceptLanguage.class);
        HEADER_ANNOTATIONS.put("Content-Type", ContentType.class);
        HEADER_ANNOTATIONS.put("Origin", Origin.class);
        HEADER_ANNOTATIONS.put("Referer", Referer.class);
        HEADER_ANNOTATIONS.put("User-Agent", UserAgent.class);

        HTTP_METHOD_ANNOTATIONS.put("GET", GET.class);
        HTTP_METHOD_ANNOTATIONS.put("POST", POST.class);
        HTTP_METHOD_ANNOTATIONS.put("DELETE", DELETE.class);
        HTTP_METHOD_ANNOTATIONS.put("HEAD", HEAD.class);
        HTTP_METHOD_ANNOTATIONS.put("PUT", PUT.class);

        PARAMETER_ANNOTATIONS.add(URL.class);
        PARAMETER_ANNOTATIONS.add(Param.class);
        PARAMETER_ANNOTATIONS.add(FilePart.class);
        PARAMETER_ANNOTATIONS.add(StringBody.class);
        PARAMETER_ANNOTATIONS.add(Header.class);
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
                URL url = (URL) annotation;
                mapper.setUrl(url.value());
            } else if (annotationType == RequestCharset.class) {
                RequestCharset requestCharset = (RequestCharset) annotation;
                mapper.setRequestCharset(requestCharset.value());
            } else if (annotationType == ResponseCharset.class) {
                ResponseCharset responseCharset = (ResponseCharset) annotation;
                mapper.setResponseCharset(responseCharset.value());
            } else if (annotationType == Proxy.class) {
                Proxy proxy = (Proxy) annotation;
                Config config = mapper.getConfig();
                if (config == null) {
                    config = new Config();
                }
                config.setProxy(proxy);
                mapper.setConfig(config);
            } else if (annotationType == Logger.class) {
                Logger logger = (Logger) annotation;
                Config config = mapper.getConfig();
                if (config == null) {
                    config = new Config();
                }
                config.setLogger(logger);
                mapper.setConfig(config);
            }
        }
        //解析请求头
        parseHeader(target).forEach(mapper::addHeader);
    }

    private void parseMethodAnnotation(Mapper mapper) {
        for (Method method : target.getDeclaredMethods()) {
            MapperMethod mapperMethod = new MapperMethod();
            mapperMethod.setName(method.getName());
            //URL
            if (Objects.nonNull(method.getAnnotation(URL.class))) {
                mapperMethod.setUrl(method.getAnnotation(URL.class).value());
            }

            //http 方法
            List<Map.Entry<String, Class<? extends Annotation>>> httpMethod = HTTP_METHOD_ANNOTATIONS.entrySet().stream()
                    .filter(entry -> Objects.nonNull(method.getAnnotation(entry.getValue())))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(httpMethod)) {
                throw new RuntimeException(String.format("没有找到HTTP请求方法：%s", method.getName()));
            }
            if (httpMethod.size() > 1) {
                throw new RuntimeException(String.format("%s重复设置HTTP请求方法。[%s]", target.getName() + "." + method.getName(), String.join(",", httpMethod.stream().map(d -> d.getValue().getSimpleName()).collect(Collectors.toList()))));
            }
            mapperMethod.setMethod(httpMethod.get(0).getKey());

            //解析方法形参
            ParamMapping parameterMapping = new ParamMapping();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < method.getParameterCount(); i++) {
                List<Annotation> annotations = Arrays.stream(parameters[i].getAnnotations())
                        .filter(a -> PARAMETER_ANNOTATIONS.contains(a.annotationType()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(annotations)) {
                    continue;
                }
                parameterMapping.put(i, annotations.get(0));
            }
            if (parameterMapping.size() != 0) {
                mapperMethod.setParamMapping(parameterMapping);
            }

            //请求头
            mapperMethod.getHeaders().putAll(parseHeader(method));

            //固定参数
            if (method.getAnnotation(Param.class) != null) {
                Param p = method.getAnnotation(Param.class);
                mapperMethod.getParams().put(p.name(), p.value());
            } else if (method.getAnnotation(Params.class) != null) {
                for (Param p : method.getAnnotation(Params.class).value()) {
                    mapperMethod.getParams().put(p.name(), p.value());
                }
            }

            if (method.getAnnotation(FilePart.class) != null) {
                FilePart f = method.getAnnotation(FilePart.class);
                mapperMethod.getFiles().put(f.name(), new File(f.value()));
            } else if (method.getAnnotation(Multiparts.class) != null) {
                for (FilePart f : method.getAnnotation(Multiparts.class).value()) {
                    mapperMethod.getFiles().put(f.name(), new File(f.value()));
                }
            }

            mapperMethod.setRequestCharset(method.getAnnotation(RequestCharset.class) == null ? null : method.getAnnotation(RequestCharset.class).value());
            mapperMethod.setResponseCharset(method.getAnnotation(ResponseCharset.class) == null ? null : method.getAnnotation(ResponseCharset.class).value());

            mapperMethod.setBody(method.getAnnotation(StringBody.class) == null ? null : method.getAnnotation(StringBody.class).value());

            mapper.addMethod(method.getName(), mapperMethod);
        }
    }

    private Map<String, String> parseHeader(AnnotatedElement element) {
        Map<String, String> result = Maps.newHashMap();
        if (Objects.nonNull(element.getAnnotation(Headers.class))) {
            Arrays.stream(element.getAnnotation(Headers.class).value())
                    .forEach(h -> result.put(h.name(), h.value()));
        }
        for (Map.Entry<String, Class<? extends Annotation>> entry : HEADER_ANNOTATIONS.entrySet()) {
            Class<? extends Annotation> c = entry.getValue();
            if (element.isAnnotationPresent(c)) {
                Annotation annotation = element.getAnnotation(c);
                try {
                    String value = (String) c.getMethod("value").invoke(annotation);
                    result.put(entry.getKey(), value);
                } catch (Exception e) {
                    throw new RuntimeException("Could not find value method on Header annotation.  Cause: " + e, e);
                }
            }
        }
        return result;
    }
}
