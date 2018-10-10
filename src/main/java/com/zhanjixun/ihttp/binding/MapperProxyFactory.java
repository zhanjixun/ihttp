package com.zhanjixun.ihttp.binding;


import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.executor.CommonsHttpClientExecutor;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import com.zhanjixun.ihttp.parsing.Parser;
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
        MapperProxy mapperProxy = new MapperProxy(mapper, new CommonsHttpClientExecutor(mapper.getConfig()));
        return newInstance(mapperProxy);
    }

    private Mapper cachedMapper(Class<T> mapperInterface) {
        Mapper mapper = mapperCache.get(mapperInterface);
        if (mapper == null) {
            Parser parser = new AnnotationParser(mapperInterface);
            mapper = parser.parse();
            mapperCache.put(mapperInterface, mapper);
        }
        return mapper;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }
}
