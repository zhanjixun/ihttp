package com.zhanjixun.ihttp.binding;


import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.domain.Configuration;
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
        Configuration configuration = mapper.getConfiguration();
        Class<? extends BaseExecutor> executorClass = configuration.getExecutor() == null ? ComponentsHttpClientExecutor.class : configuration.getExecutor();
        try {
            BaseExecutor baseExecutor = executorClass.getConstructor(Configuration.class).newInstance(configuration);
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
