package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.annotations.Retryable;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.binding.MapperParameter;
import com.zhanjixun.ihttp.domain.HttpProxy;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析注解方式Mapper
 *
 * @author zhanjixun
 */
@Slf4j
public class AnnotationParser implements Parser {

    private final Class<?> target;

    public AnnotationParser(Class<?> target) {
        this.target = target;
    }

    @Override
    public Mapper parse() {
        List<MapperMethod> mapperMethods = new ArrayList<>();
        for (Method method : target.getDeclaredMethods()) {
            MapperMethod mapperMethod = new MapperMethod(null, method.getName());
            //1.解析方法上的注解
            handleMethodAnnotation(method, mapperMethod);

            List<MapperParameter> mapperParameters = new ArrayList<>();
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = method.getParameters()[i];
                MapperParameter mapperParameter = new MapperParameter(parameter.getName(), i);
                //2.解析参数上的注解
                handleParameterAnnotation(parameter, mapperParameter);
            }
        }

        //3.解析类上面的注解
        Mapper mapper = new Mapper(target, mapperMethods);
        handleTypeAnnotation(target, mapper);

        return mapper;
    }

    private void handleTypeAnnotation(Class<?> target, Mapper mapper) {
        ReflectUtils.ifPresent(target, URL.class, e -> mapper.setUrl(e.value()));

        //配置类注解
        ReflectUtils.ifPresent(target, DisableCookie.class, e -> mapper.setDisableCookie(true));
        ReflectUtils.ifPresent(target, ResponseCharset.class, e -> mapper.setResponseCharset(e.value()));
        ReflectUtils.ifPresent(target, CookieShare.class, e -> mapper.setCookieJar(e.value()));
        ReflectUtils.ifPresent(target, HttpExecutor.class, e -> mapper.setHttpExecutor(e.value()));
        ReflectUtils.ifPresent(target, Retryable.class, e -> mapper.setRetryable(new com.zhanjixun.ihttp.parsing.Retryable(e.throwable(), e.policy(), e.maxAttempts(), e.delay(), e.multiplier())));
        ReflectUtils.ifPresent(target, Proxy.class, e -> mapper.setProxy(new HttpProxy(e.hostName(), e.port(), e.trustSSL())));

        //请求头类注解
        mapper.setHeaders(new HashMap<>());
        ReflectUtils.ifPresent(target, Accept.class, e -> mapper.getHeaders().put("Accept", e.value()));
        ReflectUtils.ifPresent(target, AcceptEncoding.class, e -> mapper.getHeaders().put("Accept-Encoding", e.value()));
        ReflectUtils.ifPresent(target, AcceptLanguage.class, e -> mapper.getHeaders().put("Accept-Language", e.value()));
        ReflectUtils.ifPresent(target, ContentType.class, e -> mapper.getHeaders().put("Content-Type", e.value()));
        ReflectUtils.ifPresent(target, Origin.class, e -> mapper.getHeaders().put("Origin", e.value()));
        ReflectUtils.ifPresent(target, Referer.class, e -> mapper.getHeaders().put("Referer", e.value()));
        ReflectUtils.ifPresent(target, UserAgent.class, e -> mapper.getHeaders().put("User-Agent", e.value()));
        ReflectUtils.ifPresentMulti(target, Header.class, e -> Arrays.stream(e).forEach(a -> mapper.getHeaders().put(a.name(), a.value())));

        //生成类注解
        ReflectUtils.ifPresentMulti(target, RandomParam.class, e -> {
            mapper.setRandomParams(Arrays.stream(e).map(a -> new Random(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList()));
        });
        ReflectUtils.ifPresentMulti(target, RandomPlaceholder.class, e -> {
            mapper.setRandomPlaceholders(Arrays.stream(e).map(a -> new Random(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList()));
        });
        ReflectUtils.ifPresentMulti(target, TimestampParam.class, e -> {
            mapper.setTimestampParams(Arrays.stream(e).map(a -> new Timestamp(a.name(), a.unit())).collect(Collectors.toList()));
        });
        ReflectUtils.ifPresentMulti(target, TimestampPlaceholder.class, e -> {
            mapper.setTimestampParams(Arrays.stream(e).map(a -> new Timestamp(a.name(), a.unit())).collect(Collectors.toList()));
        });
    }

    private void handleMethodAnnotation(Method method, MapperMethod mapperMethod) {

    }

    private void handleParameterAnnotation(Parameter parameter, MapperParameter mapperParameter) {

    }

}
