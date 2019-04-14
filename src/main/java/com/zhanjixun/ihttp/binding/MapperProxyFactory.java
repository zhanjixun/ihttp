package com.zhanjixun.ihttp.binding;


import com.google.common.collect.Maps;
import com.google.common.reflect.Reflection;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.executor.*;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
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
            MapperProxy mapperProxy = new MapperProxy(mapper, newExecutor(executorClass, configuration));
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

    protected T newInstance(MapperProxy mapperProxy) {
        return Reflection.newProxy(mapperInterface, mapperProxy);
    }

    private Executor newExecutor(Class<? extends BaseExecutor> executorClass, Configuration configuration) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (executorClass == ComponentsHttpClientExecutor.class) {
            return new ComponentsHttpClientExecutor(configuration);
        }
        if (executorClass == CommonsHttpClientExecutor.class) {
            return new CommonsHttpClientExecutor(configuration);
        }
        if (executorClass == OkHttpExecutor.class) {
            return new OkHttpExecutor(configuration);
        }
        if (executorClass == JavaExecutor.class) {
            return new JavaExecutor(configuration);
        }
        return executorClass.getConstructor(Configuration.class).newInstance(configuration);
    }
}
