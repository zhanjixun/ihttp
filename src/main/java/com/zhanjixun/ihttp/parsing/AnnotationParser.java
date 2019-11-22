package com.zhanjixun.ihttp.parsing;

import com.zhanjixun.ihttp.binding.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析注解方式Mapper
 *
 * @author zhanjixun
 */
@Slf4j
public class AnnotationParser implements Parser {

    private Map<Class<? extends Annotation>, AnnotationHandler> handlers = new HashMap<>();

    @Override
    public Mapper parse() {

        return null;
    }


}
