package com.zhanjixun.ihttp.binding;


import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.cookie.CookiesStoreFactory;
import com.zhanjixun.ihttp.domain.Configuration;
import com.zhanjixun.ihttp.executor.ComponentsHttpClientExecutor;
import com.zhanjixun.ihttp.executor.Executor;
import com.zhanjixun.ihttp.parsing.AnnotationParser;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

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

		Class<? extends Executor> executorClass = ObjectUtils.defaultIfNull(configuration.getExecutor(), ComponentsHttpClientExecutor.class);

		try {
			CookiesStore cookiesStore = new CookiesStoreFactory().createCookiesStore(mapperInterface);
			Executor executor = executorClass.getConstructor(Configuration.class, CookiesStore.class).newInstance(configuration, cookiesStore);
			MapperProxy mapperProxy = new MapperProxy(mapper, executor);
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
