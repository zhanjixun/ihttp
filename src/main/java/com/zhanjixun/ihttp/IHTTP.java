package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.binding.MapperProxyFactory;

import java.util.Arrays;
import java.util.Objects;

import static com.zhanjixun.ihttp.parsing.AnnotationParser.HTTP_METHOD_ANNOTATIONS;

/**
 * 系统入口类
 *
 * @author zhanjixun
 */
public class IHTTP {

	@SuppressWarnings("unchecked")
	public static <T> T getMapper(Class<T> mapperInterface) {
		try {
			MapperProxyFactory<T> mapperProxyFactory = new MapperProxyFactory(mapperInterface);
			return mapperProxyFactory.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
		}
	}

	public static <T> boolean isMapper(Class<T> mapperClass) {
		return Arrays.stream(mapperClass.getDeclaredMethods()).parallel()
				.anyMatch(m -> HTTP_METHOD_ANNOTATIONS.stream().anyMatch(a -> Objects.nonNull(m.getAnnotation(a))));
	}

}
