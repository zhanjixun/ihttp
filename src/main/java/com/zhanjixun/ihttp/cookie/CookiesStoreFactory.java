package com.zhanjixun.ihttp.cookie;

import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.annotations.CookieJar;
import com.zhanjixun.ihttp.annotations.DisableCookie;
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

    public CookiesStore createCookiesStore(Class<?> mapperType) {
        if (mapperType.isAnnotationPresent(DisableCookie.class)) {
            return new DisableCookiesStoreImpl();
        }
        CookieJar cookieJar = mapperType.getAnnotation(CookieJar.class);
        String key = cookieJar == null ? "PUBLIC_COOKIES_STORE:" + mapperType.getName() : "PRIVATE_COOKIES_STORE:" + cookieJar.value();
        return cookiesStoreMap.computeIfAbsent(key, k -> new CookiesStoreImpl());
    }
}
