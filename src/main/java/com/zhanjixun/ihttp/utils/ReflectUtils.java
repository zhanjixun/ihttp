package com.zhanjixun.ihttp.utils;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:35
 */
public class ReflectUtils {

	/**
	 * 如果有这个注解，则调用消费者
	 *
	 * @param target
	 * @param annotationClass
	 * @param consumer
	 * @param <T>
	 */
	public static <T extends Annotation> void containsAnnotation(AnnotatedElement target, Class<T> annotationClass, Consumer<T> consumer) {
		if (target.getAnnotation(annotationClass) != null) {
			consumer.accept(target.getAnnotation(annotationClass));
		}
	}


	/**
	 * 一个类型是基本类型或者其封装类
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isPrimitive(Object obj) {
		try {
			return ((Class<?>) obj.getClass().getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取Repeatable注解
	 *
	 * @param <T>
	 * @param target
	 * @param annotationClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> List<T> getRepeatableAnnotation(AnnotatedElement target, Class<T> annotationClass) {
		T annotation = target.getAnnotation(annotationClass);
		//当可重复注解只使用一次的时候
		if (annotation != null) {
			return Lists.newArrayList(annotation);
		}
		//重复使用多次的时候
		Repeatable repeatable = annotationClass.getAnnotation(Repeatable.class);
		Annotation repeatableAnnotation = target.getAnnotation(repeatable.value());
		if (repeatableAnnotation == null) {
			return Collections.emptyList();
		}

		try {
			Object[] objects = (Object[]) invokeAnnotationMethod(repeatableAnnotation, "value");
			return Arrays.stream(objects).map(o -> (T) o).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public static Object invokeAnnotationMethod(Annotation annotation, String method) {
		try {
			return annotation.annotationType().getMethod(method).invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException("Could not invoke value method.  Cause: " + e, e);
		}
	}
}
