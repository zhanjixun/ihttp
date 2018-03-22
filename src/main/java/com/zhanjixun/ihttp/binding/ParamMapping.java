package com.zhanjixun.ihttp.binding;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.parsing.AnnotationParser;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Mapper参数映射
 *
 * @author zhanjixun
 * @see com.zhanjixun.ihttp.annotations.URL
 * @see com.zhanjixun.ihttp.annotations.Header
 * @see com.zhanjixun.ihttp.annotations.StringBody
 * @see com.zhanjixun.ihttp.annotations.Param
 * @see com.zhanjixun.ihttp.annotations.FilePart
 * @see AnnotationParser#HEADER_ANNOTATIONS
 */
public class ParamMapping {

    private Map<Integer, Annotation> mapping = Maps.newHashMap();

    public Annotation get(Integer index) {
        return mapping.get(index);
    }

    public int size() {
        return mapping.size();
    }

    public void put(Integer index, Annotation annotation) {
        mapping.put(index, annotation);
    }

    public Annotation[] paramTypes() {
        Annotation[] paramTypes = new Annotation[mapping.size()];
        for (int i = 0; i < mapping.size(); i++) {
            paramTypes[i] = mapping.get(i);
        }
        return paramTypes;
    }

}
