package com.zhanjixun.ihttp.cookie;

import com.google.common.collect.Maps;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.annotations.CookieShare;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author :zhanjixun
 * @date : 2019/04/15 17:53
 * @contact :zhanjixun@qq.com
 */
@Slf4j
public class CookiesStoreFactory {

	//这里存放系统中所有的CookiesStore
	private final static Map<String, CookiesStore> cookiesStoreMap = Maps.newConcurrentMap();

	public CookiesStore createCookiesStore(Class<?> mapperClass) {
		CookieShare cookieShare = mapperClass.getAnnotation(CookieShare.class);
		String key = cookieShare == null ? "selfuse:" + mapperClass.getName() : "share:" + cookieShare.value();

		CookiesStore cookiesStore = cookiesStoreMap.get(key);
		if (cookiesStore == null) {
			cookiesStore = new CookiesStoreImpl();
			cookiesStoreMap.put(key, cookiesStore);
		}
		return cookiesStore;
	}
}
