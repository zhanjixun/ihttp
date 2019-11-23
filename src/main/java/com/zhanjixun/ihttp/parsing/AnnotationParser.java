package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.annotations.Retryable;
import com.zhanjixun.ihttp.annotations.*;
import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.binding.MapperParameter;
import com.zhanjixun.ihttp.utils.ReflectUtils;
import com.zhanjixun.ihttp.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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
		ReflectUtils.ifPresent(target, Proxy.class, e -> mapper.setHttpProxy(new HttpProxy(e.hostName(), e.port(), e.trustSSL())));

		//请求头类注解
		mapper.setRequestHeaders(handlerRequestHeader(target));

		//生成类注解
		ReflectUtils.ifPresentMulti(target, RandomParam.class, e -> mapper.setRandomGeneratorParams(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(target, RandomPlaceholder.class, e -> mapper.setRandomGeneratorPlaceholders(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(target, TimestampParam.class, e -> mapper.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(target, TimestampPlaceholder.class, e -> mapper.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
	}

	private void handleMethodAnnotation(Method method, MapperMethod mapperMethod) {
		ReflectUtils.ifPresent(method, URL.class, e -> mapperMethod.setUrl(e.value()));

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

		//HTTP请求头类注解
		mapperMethod.setRequestHeaders(handlerRequestHeader(method));

		//HTTP参数
		ReflectUtils.ifPresentMulti(method, Param.class, e -> mapperMethod.setRequestParams(Arrays.stream(e).collect(Collectors.toMap(Param::name, a -> a.encode() ? StrUtils.URLEncoder(a.value(), mapperMethod.getRequestCharset()) : a.value()))));
		ReflectUtils.ifPresentMulti(method, FilePart.class, e -> mapperMethod.setRequestMultiParts(Arrays.stream(e).collect(Collectors.toMap(FilePart::name, FilePart::value))));
		ReflectUtils.ifPresent(method, StringBody.class, e -> mapperMethod.setRequestBody(e.value()));

		//配置类注解
		ReflectUtils.ifPresent(method, DisableCookie.class, e -> mapperMethod.setDisableCookie(true));
		ReflectUtils.ifPresent(method, ResponseCharset.class, e -> mapperMethod.setResponseCharset(e.value()));
		ReflectUtils.ifPresent(method, Retryable.class, e -> mapperMethod.setRetryable(new com.zhanjixun.ihttp.parsing.Retryable(e.throwable(), e.policy(), e.maxAttempts(), e.delay(), e.multiplier())));
		ReflectUtils.ifPresent(method, AssertStatusCode.class, e -> mapperMethod.setAssertStatusCode(e.value()));

		//生成类注解
		ReflectUtils.ifPresentMulti(method, RandomParam.class, e -> mapperMethod.setRandomGeneratorParams(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(method, RandomPlaceholder.class, e -> mapperMethod.setRandomGeneratorPlaceholders(Arrays.stream(e).map(a -> new RandomGenerator(a.name(), a.length(), a.chars(), a.encode())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(method, TimestampParam.class, e -> mapperMethod.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(method, TimestampPlaceholder.class, e -> mapperMethod.setTimestampGeneratorParams(Arrays.stream(e).map(a -> new TimestampGenerator(a.name(), a.unit())).collect(Collectors.toList())));
	}

	private void handleParameterAnnotation(Parameter parameter, MapperParameter mapperParameter) {
		ReflectUtils.ifPresent(parameter, URL.class, e -> mapperParameter.setURLAnnotated(true));

		//HTTP请求头类注解
		mapperParameter.setRequestHeaderNames(new ArrayList<>(handlerRequestHeader(parameter).keySet()));

		ReflectUtils.ifPresentMulti(parameter, Param.class, e -> mapperParameter.setRequestParamNames(Arrays.stream(e).map(a -> new EncodableString(a.name(), a.encode())).collect(Collectors.toList())));
		ReflectUtils.ifPresentMulti(parameter, FilePart.class, e -> mapperParameter.setRequestMultiPartNames(Arrays.stream(e).map(FilePart::name).collect(Collectors.toList())));
		ReflectUtils.ifPresent(parameter, StringBody.class, e -> mapperParameter.setRequestBody(new EncodableObject(e.encode())));

		ReflectUtils.ifPresent(parameter, Placeholder.class, e -> mapperParameter.setPlaceholder(new EncodableString(e.value(), e.encode())));
	}

	private Map<String, String> handlerRequestHeader(AnnotatedElement annotatedElement) {
		Map<String, String> requestHeaders = new HashMap<>();
		ReflectUtils.ifPresent(annotatedElement, Accept.class, e -> requestHeaders.put("Accept", e.value()));
		ReflectUtils.ifPresent(annotatedElement, AcceptEncoding.class, e -> requestHeaders.put("Accept-Encoding", e.value()));
		ReflectUtils.ifPresent(annotatedElement, AcceptLanguage.class, e -> requestHeaders.put("Accept-Language", e.value()));
		ReflectUtils.ifPresent(annotatedElement, ContentType.class, e -> requestHeaders.put("Content-Type", e.value()));
		ReflectUtils.ifPresent(annotatedElement, Origin.class, e -> requestHeaders.put("Origin", e.value()));
		ReflectUtils.ifPresent(annotatedElement, Referer.class, e -> requestHeaders.put("Referer", e.value()));
		ReflectUtils.ifPresent(annotatedElement, UserAgent.class, e -> requestHeaders.put("User-Agent", e.value()));
		ReflectUtils.ifPresentMulti(annotatedElement, Header.class, e -> Arrays.stream(e).forEach(a -> requestHeaders.put(a.name(), a.value())));
		return requestHeaders;
	}
}
