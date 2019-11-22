package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.binding.Mapper;
import com.zhanjixun.ihttp.binding.MapperMethod;
import com.zhanjixun.ihttp.binding.MapperParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author :zhanjixun
 * @date : 2019/11/22 19:51
 */
public interface AnnotationHandler<T extends Annotation> {

    void handleAnnotationOnType(AnnotatedElement annotatedElement, T annotation, Mapper mapper);

    void handleAnnotationOnMethod(AnnotatedElement annotatedElement, T annotation, MapperMethod mapperMethod);

    void handleAnnotationOnParameter(AnnotatedElement annotatedElement, T annotation, MapperParameter mapperParameter);

}
