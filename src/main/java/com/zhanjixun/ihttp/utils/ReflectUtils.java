package com.zhanjixun.ihttp.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:35
 */
public class ReflectUtils {
    /**
     * 获取方法或类上面的注解
     * 优先使用方法上
     *
     * @param type
     * @param method
     * @param annotationType
     * @return
     */
    public static <T extends Annotation> T[] getAnnotationsMethodFirst(Class<?> type, Method method, Class<T> annotationType) {
        //方法体上的注解
        T[] annotationsOnMethod = method.getAnnotationsByType(annotationType);
        if (annotationsOnMethod != null && annotationsOnMethod.length > 0) {
            return annotationsOnMethod;
        }

        //类上面的注解
        T[] annotationsOnClass = type.getAnnotationsByType(annotationType);
        if (annotationsOnClass != null && annotationsOnClass.length > 0) {
            return annotationsOnClass;
        }

        return null;
    }

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

    public static Object invokeAnnotationMethod(Annotation annotation, String method) {
        try {
            return annotation.annotationType().getMethod(method).invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException("Could not invoke " + method + " method.  Cause: " + e, e);
        }
    }
}
