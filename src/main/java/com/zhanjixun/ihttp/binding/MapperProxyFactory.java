package com.zhanjixun.ihttp.binding;


import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.annotations.HttpExecutor;
import com.zhanjixun.ihttp.executor.BaseExecutor;
import com.zhanjixun.ihttp.executor.ComponentsHttpClientExecutor;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import lombok.Getter;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Mapper代理工厂
 *
 * @author zhanjixun
 */
public class MapperProxyFactory<T> {

    @Getter
    private final Class<T> mapperInterface;

    private final Map<Class<T>, Mapper> mapperCache = Maps.newHashMap();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance() {
        Mapper mapper = cachedMapper(mapperInterface);
        HttpExecutor annotation = mapperInterface.getAnnotation(HttpExecutor.class);
        Class<? extends BaseExecutor> executorClass = mapperInterface.getAnnotation(HttpExecutor.class) == null ? ComponentsHttpClientExecutor.class : annotation.value();
        try {
            BaseExecutor baseExecutor = executorClass.newInstance();
            MapperProxy mapperProxy = new MapperProxy(mapper, baseExecutor);
            return newInstance(mapperProxy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mapper cachedMapper(Class<T> mapperInterface) {
        Mapper mapper = mapperCache.get(mapperInterface);
        if (mapper == null) {
            mapper = new AnnotationParser(mapperInterface).parse();
            mapperCache.put(mapperInterface, mapper);
        }
        return mapper;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }
}
