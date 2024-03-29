package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.binding.MapperParameter;
import com.zhanjixun.ihttp.domain.FormDatas;
import com.zhanjixun.ihttp.domain.Header;
import com.zhanjixun.ihttp.domain.MultipartFile;
import com.zhanjixun.ihttp.domain.Param;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
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
            MapperMethod mapperMethod = new MapperMethod(method.getName());
            mapperMethod.setReturnType(method.getReturnType());
            //1.解析方法上的注解
            handleMethodAnnotation(method, mapperMethod);

            MapperParameter[] mapperParameters = new MapperParameter[method.getParameterCount()];
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = method.getParameters()[i];
                MapperParameter mapperParameter = new MapperParameter(parameter.getName(), i, parameter.getType());
                //2.解析参数上的注解
                handleParameterAnnotation(parameter, mapperParameter);
                mapperParameters[i] = mapperParameter;
            }
            mapperMethod.setParameters(mapperParameters);

            mapperMethods.add(mapperMethod);
        }

        //3.解析类上面的注解
        Mapper mapper = new Mapper(target, mapperMethods);
        mapperMethods.forEach(m -> m.setMapper(mapper));
        handleTypeAnnotation(target, mapper);

        return mapper;
    }

    private void handleTypeAnnotation(Class<?> target, Mapper mapper) {
        ReflectUtils.ifPresent(target, URL.class, e -> mapper.setUrl(e.value()));

        //配置类注解
        ReflectUtils.ifPresent(target, DisableCookie.class, e -> mapper.setDisableCookie(true));
        ReflectUtils.ifPresent(target, Proxy.class, e -> mapper.setHttpProxy(new HttpProxy(e.hostName(), e.port(), e.trustSSL())));

        //请求头类注解
        mapper.setRequestHeaders(handlerRequestHeader(target));

        //生成类注解
        ReflectUtils.ifPresentMulti(target, RandomRequestParam.class, e -> mapper.setRandomGeneratorParams(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(target, RandomPlaceholder.class, e -> mapper.setRandomGeneratorPlaceholders(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(target, TimestampRequestParam.class, e -> mapper.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(target, TimestampPlaceholder.class, e -> mapper.setTimestampGeneratorPlaceholders(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
    }

    private void handleMethodAnnotation(Method method, MapperMethod mapperMethod) {
        if (method.getAnnotation(URL.class) != null) {
            mapperMethod.setUrl(method.getAnnotation(URL.class).value());
        }

        //HTTP方法类注解
        ReflectUtils.ifPresent(method, GET.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setFollowRedirects(e.followRedirects());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, POST.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, PUT.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, DELETE.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, HEAD.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setFollowRedirects(e.followRedirects());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, OPTIONS.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, TRACE.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });
        ReflectUtils.ifPresent(method, TRACE.class, e -> {
            mapperMethod.setRequestMethod(e.annotationType().getSimpleName());
            mapperMethod.setRequestCharset(e.charset());
        });

        //HTTP请求头类注解
        mapperMethod.setRequestHeaders(handlerRequestHeader(method));

        //HTTP参数
        ReflectUtils.ifPresentMulti(method, RequestParam.class, e -> {
            List<Param> params = new ArrayList<>();
            for (RequestParam requestParam : e) {
                String value = requestParam.encode() ? StrUtils.URLEncoder(requestParam.value(), mapperMethod.getRequestCharset()) : requestParam.value();
                params.add(new Param(requestParam.name(), value));
            }
            mapperMethod.setRequestParams(params);
        });

        if (method.getAnnotationsByType(RequestPart.class) != null) {
            List<FormDatas> requestMultiParts = new ArrayList<>();
            for (RequestPart requestPart : method.getAnnotationsByType(RequestPart.class)) {
                File file = new File(requestPart.value());
                requestMultiParts.add(new FormDatas(requestPart.name(), MultipartFile.create(file)));
            }
            mapperMethod.setRequestMultiParts(requestMultiParts);
        }

        ReflectUtils.ifPresent(method, RequestBody.class, e -> mapperMethod.setRequestBody(e.value()));
        //配置类注解
        ReflectUtils.ifPresent(method, DisableCookie.class, e -> mapperMethod.setDisableCookie(true));
        //生成类注解
        ReflectUtils.ifPresentMulti(method, RandomRequestParam.class, e -> mapperMethod.setRandomGeneratorParams(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(method, RandomPlaceholder.class, e -> mapperMethod.setRandomGeneratorPlaceholders(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(method, TimestampRequestParam.class, e -> mapperMethod.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(method, TimestampPlaceholder.class, e -> mapperMethod.setTimestampGeneratorPlaceholders(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
    }

    private void handleParameterAnnotation(Parameter parameter, MapperParameter mapperParameter) {
        ReflectUtils.ifPresent(parameter, URL.class, e -> mapperParameter.setURLAnnotated(true));

        //HTTP请求头类注解
        mapperParameter.setRequestHeaderNames(handlerRequestHeader(parameter).stream().map(Header::getName).collect(Collectors.toList()));

        ReflectUtils.ifPresentMulti(parameter, RequestParam.class, e -> mapperParameter.setRequestParamNames(Arrays.stream(e).map(a -> new EncodableString(a.name(), a.encode())).collect(Collectors.toList())));
        ReflectUtils.ifPresentMulti(parameter, RequestPart.class, e -> mapperParameter.setRequestMultiPartNames(Arrays.stream(e).map(RequestPart::name).collect(Collectors.toList())));
        ReflectUtils.ifPresent(parameter, RequestBody.class, e -> mapperParameter.setRequestBody(new EncodableObject(e.encode())));

        ReflectUtils.ifPresent(parameter, Placeholder.class, e -> mapperParameter.setPlaceholder(new EncodableString(e.value(), e.encode())));
    }

    private List<Header> handlerRequestHeader(AnnotatedElement annotatedElement) {
        List<Header> headers = new ArrayList<>();
        ReflectUtils.ifPresentMulti(annotatedElement, RequestHeader.class, e -> Arrays.stream(e).forEach(a -> headers.add(new Header(a.name(), a.value()))));
        return headers;
    }
}
