package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.domain.Cookie;

import java.io.File;
import java.util.List;

/**
 * cookie商店
 * Mapper继承此接口获得操作Cookie的能力
 *
 * @author :zhanjixun
 * @date : 2018/9/28 13:41
 * @see org.springframework.beans.BeanUtils
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
}
