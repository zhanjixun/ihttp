package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.annotations.CookieJar;
import com.zhanjixun.ihttp.annotations.DisableCookie;
import com.zhanjixun.ihttp.cookie.Cookie;
import com.zhanjixun.ihttp.cookie.CookiesStoreImpl;
import com.zhanjixun.ihttp.cookie.DisableCookiesStoreImpl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cookie商店
 * Mapper继承此接口获得操作Cookie的能力
 *
 * @author :zhanjixun
 * @date : 2018/9/28 13:41
 */
public interface CookiesStore {
    /**
     * 增加cookie
     *
     * @param cookie 需要增加的cookie
     */
    void addCookies(List<Cookie> cookie);

    /**
     * 增加cookie
     *
     * @param cookie 需要增加的cookie
     */
    void addCookie(Cookie cookie);

    /**
     * 移除Cookie
     *
     * @param cookie
     * @return
     */
    boolean remove(Cookie cookie);

    /**
     * 获取所有cookie
     *
     * @return 所有cookie
     */
    List<Cookie> getCookies();

    /**
     * 清除过期Cookie
     *
     * @return
     */
    boolean clearExpired();

    /**
     * 清除过期Cookie
     *
     * @param date
     * @return
     */
    boolean clearExpired(Date date);

    /**
     * 清除所有cookie
     */
    void clearCookies();

    /**
     * 缓存cookie到本地文件中
     *
     * @param cacheFile
     */
    void cacheCookie(File cacheFile);

    /**
     * 加载本地cookie缓存
     *
     * @param cacheFile
     * @return 加载了多少条cookie
     */
    int loadCookieCache(File cacheFile);
    
    interface Factory {

        CookiesStore create(Class<?> type);

    }

    class Default implements Factory {
        /**
         * 这里存放系统中所有的CookiesStore
         */
        private final static Map<String, CookiesStore> cookiesStoreMap = new ConcurrentHashMap<>();

        @Override
        public CookiesStore create(Class<?> mapperType) {
            if (mapperType.isAnnotationPresent(DisableCookie.class)) {
                return new DisableCookiesStoreImpl();
            }
            CookieJar cookieJar = mapperType.getAnnotation(CookieJar.class);
            String key = cookieJar == null ? "PUBLIC_COOKIES_STORE:" + mapperType.getName() : "PRIVATE_COOKIES_STORE:" + cookieJar.value();
            return cookiesStoreMap.computeIfAbsent(key, k -> new CookiesStoreImpl());
        }
    }
}
