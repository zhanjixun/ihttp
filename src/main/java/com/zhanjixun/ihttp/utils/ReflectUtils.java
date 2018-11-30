package com.zhanjixun.ihttp.utils;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:35
 */
public class ReflectUtils {
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
            Object[] objects = (Object[]) repeatableAnnotation.annotationType().getMethod("value").invoke(repeatableAnnotation);
            return Arrays.stream(objects).map(o -> (T) o).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
