package com.zhanjixun.ihttp.utils;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * @author :zhanjixun
 * @date : 2018/11/26 14:35
 */
public class ReflectUtils {

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
     * @param target
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> List<T> getRepeatableAnnotation(AnnotatedElement target, Class<T> annotationClass) {
        List<T> list = Lists.newArrayList();
        T annotation = target.getAnnotation(annotationClass);
        //当可重复注解只使用一次的时候
        if (annotation != null) {
            list.add(annotation);
            return list;
        }
        //重复使用多次的时候
        Repeatable repeatable = annotationClass.getAnnotation(Repeatable.class);
        Annotation repeatableAnnotation = target.getAnnotation(repeatable.value());
        try {
            Object[] objects = (Object[]) repeatableAnnotation.annotationType().getMethod("value").invoke(repeatableAnnotation);
            for (Object o : objects) {
                list.add((T) o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
