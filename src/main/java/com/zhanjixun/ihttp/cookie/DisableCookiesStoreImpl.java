package com.zhanjixun.ihttp.cookie;

import com.zhanjixun.ihttp.CookiesStore;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 类级别禁用Cookie的实现
 *
 * @author zhanjixun
 * @date 2020-12-15 14:30:51
 */
public class DisableCookiesStoreImpl implements CookiesStore {

    @Override
    public void addCookies(List<Cookie> cookie) {
        //nothing to do
    }

    @Override
    public void addCookie(Cookie cookie) {
        //nothing to do
    }

    @Override
    public boolean remove(Cookie cookie) {
        return true;
    }

    @Override
    public List<Cookie> getCookies() {
        return Collections.emptyList();
    }

    @Override
    public boolean clearExpired() {
        return true;
    }

    @Override
    public boolean clearExpired(Date date) {
        return true;
    }

    @Override
    public void clearCookies() {
        //nothing to do
    }

    @Override
    public void cacheCookie(File cacheFile) {
        //nothing to do
    }

    @Override
    public int loadCookieCache(File cacheFile) {
        return 0;
    }
}
