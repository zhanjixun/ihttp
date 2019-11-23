package com.zhanjixun.ihttp.cookie;

import com.zhanjixun.ihttp.CookiesStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :zhanjixun
 * @date : 2019/04/15 17:53
 * @contact :zhanjixun@qq.com
 */
@Slf4j
public class CookiesStoreFactory {

	//这里存放系统中所有的CookiesStore
	private final static Map<String, CookiesStore> cookiesStoreMap = new ConcurrentHashMap<>();

	public CookiesStore createCookiesStore(Class<?> mapperType, String cookieJarId) {
		String key = cookieJarId == null ? "sys:" + mapperType.getName() : "common:" + cookieJarId;
		CookiesStore cookiesStore = cookiesStoreMap.get(key);
		if (cookiesStore == null) {
			cookiesStore = new CookiesStoreImpl();
			cookiesStoreMap.put(key, cookiesStore);
		}
		return cookiesStore;
	}
}
