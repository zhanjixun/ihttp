package com.zhanjixun.ihttp.binding;


import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.cookie.CookiesStoreFactory;
import com.zhanjixun.ihttp.executor.Executor;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import com.zhanjixun.ihttp.parsing.Configuration;
import lombok.Getter;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper代理工厂
 *
 * @author zhanjixun
 */
public class MapperProxyFactory<T> {

    @Getter
    private final Class<T> mapperInterface;

    private final Map<Class<T>, Mapper> mapperCache = new HashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance() {
        Mapper mapper = cachedMapper(mapperInterface);
        try {
            Configuration configuration = Configuration.getDefault();
            if (mapper.getHttpExecutor() != null) {
                configuration.setExecutor(mapper.getHttpExecutor());
            }
            if (mapper.getHttpProxy() != null) {
                configuration.setProxy(mapper.getHttpProxy());
            }
            if (mapper.getDisableCookie() != null) {
                configuration.setCookieEnable(mapper.getDisableCookie());
            }
            CookiesStore cookiesStore = new CookiesStoreFactory().createCookiesStore(mapperInterface);
            Executor executor = configuration.getExecutor().getConstructor(Configuration.class, CookiesStore.class).newInstance(configuration, cookiesStore);
            mapper.setExecutor(executor);

            MapperProxy mapperProxy = new MapperProxy(mapper);
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
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

}
