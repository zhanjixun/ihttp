package com.zhanjixun.ihttp.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Consumer;

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
    public static <T extends Annotation> void ifPresent(AnnotatedElement target, Class<T> annotationClass, Consumer<T> consumer) {
        if (target.isAnnotationPresent(annotationClass)) {
            consumer.accept(target.getAnnotation(annotationClass));
        }
    }

    /**
     * 如果存在复用注解，则调用消费者
     *
     * @param target
     * @param annotationClass
     * @param consumer
     * @param <T>
     */
    public static <T extends Annotation> void ifPresentMulti(AnnotatedElement target, Class<T> annotationClass, Consumer<T[]> consumer) {
        T[] annotationsByType = target.getAnnotationsByType(annotationClass);
        if (annotationClass != null) {
            consumer.accept(annotationsByType);
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
     * 判断一个类型是不是string或者基本类型及其封装类
     *
     * @param type
     * @return
     */
    public static boolean isStringOrPrimitive(Class<?> type) {
        try {
            return type == String.class || ((Class<?>) type.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static Object invokeAnnotationMethod(Annotation annotation, String method) {
        try {
            return annotation.annotationType().getMethod(method).invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException("Could not invoke " + method + " method.  Cause: " + e, e);
        }
    }
}
