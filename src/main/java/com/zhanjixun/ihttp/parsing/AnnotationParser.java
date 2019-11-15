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
import lombok.extern.slf4j.Slf4j;
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
 * 解析注解方式Mapper
 *
 * @author zhanjixun
 */
@Slf4j
public class AnnotationParser implements Parser {

	private Class<?> target;
	public static final Map<String, Class<? extends Annotation>> HEADER_ANNOTATIONS = Maps.newHashMap();
	public static final List<Class<? extends Annotation>> HTTP_METHOD_ANNOTATIONS = Lists.newArrayList();
	public static final List<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Lists.newArrayList();
	public static final List<Class<? extends Annotation>> GENERATE_ANNOTATIONS = Lists.newArrayList();

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
		HTTP_METHOD_ANNOTATIONS.add(PUT.class);
		HTTP_METHOD_ANNOTATIONS.add(DELETE.class);

		PARAMETER_ANNOTATIONS.add(URL.class);
		PARAMETER_ANNOTATIONS.add(Param.class);
		PARAMETER_ANNOTATIONS.add(FilePart.class);
		PARAMETER_ANNOTATIONS.add(StringBody.class);
		PARAMETER_ANNOTATIONS.add(StringBodyObject.class);
		PARAMETER_ANNOTATIONS.add(Header.class);
		PARAMETER_ANNOTATIONS.add(ParamMap.class);
		PARAMETER_ANNOTATIONS.add(Placeholder.class);
		PARAMETER_ANNOTATIONS.addAll(HEADER_ANNOTATIONS.values());

		GENERATE_ANNOTATIONS.add(RandomParam.class);
		GENERATE_ANNOTATIONS.add(TimestampParam.class);
		GENERATE_ANNOTATIONS.add(RandomPlaceholder.class);
		GENERATE_ANNOTATIONS.add(TimestampPlaceholder.class);
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
		ReflectUtils.ifAnnotationPresent(target, Proxy.class, p -> mapper.getConfiguration().setProxy(new HttpProxy(p.hostName(), p.port(), p.trustSSL())));
		ReflectUtils.ifAnnotationPresent(target, HttpExecutor.class, e -> mapper.getConfiguration().setExecutor(e.value()));
		ReflectUtils.ifAnnotationPresent(target, DisableCookie.class, e -> mapper.getConfiguration().setCookieEnable(false));

		ReflectUtils.ifAnnotationPresent(target, URL.class, e -> mapper.setCommonUrl(e.value()));
		//解析请求头
		mapper.getCommonHeaders().addAll(parseHeader(target));
	}

	private MapperMethod parseMethodAnnotation(Method method, Mapper mapper) {
		MapperMethod mapperMethod = new MapperMethod();
		mapperMethod.setName(method.getName());
		//URL
		ReflectUtils.ifAnnotationPresent(method, URL.class, annotation -> mapperMethod.setUrl(annotation.value()));

		//http方法
		List<? extends Annotation> httpMethod = HTTP_METHOD_ANNOTATIONS.stream().map(method::getAnnotation).filter(Objects::nonNull).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(httpMethod)) {
			throw new RuntimeException(String.format("没有找到HTTP请求方法 %s", target.getName() + "." + method.getName()));
		}
		if (httpMethod.size() > 1) {
			throw new RuntimeException(String.format("重复设置HTTP请求方法 %s[%s]", target.getName() + "." + method.getName(), httpMethod.stream().map(d -> d.annotationType().getSimpleName()).collect(Collectors.joining(","))));
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
		//返回值编码
		ReflectUtils.ifAnnotationPresent(method, ResponseCharset.class, a -> mapperMethod.setResponseCharset(a.value()));

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

		mapperMethod.setGenerate(GENERATE_ANNOTATIONS.stream().flatMap(a -> Arrays.stream(method.getAnnotationsByType(a))).toArray(Annotation[]::new));

		//请求头
		mapperMethod.getHeaders().addAll(parseHeader(method));

		//固定参数

		for (Param param : method.getAnnotationsByType(Param.class)) {
			String value = param.encode() ? StrUtils.URLEncoder(param.value(), mapperMethod.getCharset()) : param.value();
			mapperMethod.getParams().add(new NameValuePair(param.name(), value));
		}
		//文件上传
		for (FilePart filePart : method.getAnnotationsByType(FilePart.class)) {
			mapperMethod.getFileParts().add(new FileParts(filePart.name(), new File(filePart.value())));
		}
		//直接请求体
		mapperMethod.setStringBody(method.getAnnotation(StringBody.class) == null ? null : method.getAnnotation(StringBody.class).value());

		return mapperMethod;
	}

	private List<NameValuePair> parseHeader(AnnotatedElement element) {
		List<NameValuePair> headers = Lists.newArrayList();

		for (Header header : element.getAnnotationsByType(Header.class)) {
			headers.add(new NameValuePair(header.name(), header.value()));
		}

		for (Map.Entry<String, Class<? extends Annotation>> entry : HEADER_ANNOTATIONS.entrySet()) {
			ReflectUtils.ifAnnotationPresent(element, entry.getValue(), annotation -> {
				String value = (String) ReflectUtils.invokeAnnotationMethod(annotation, "value");
				headers.add(new NameValuePair(entry.getKey(), value));
			});
		}
		return headers;
	}
}
