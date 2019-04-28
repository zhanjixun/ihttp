package com.zhanjixun.ihttp.cookie;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.domain.Cookie;
import lombok.extern.log4j.Log4j;
import okio.Okio;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 接管Executor的Cookie
 *
 * @author :zhanjixun
 * @date : 2019/04/13 11:24
 * @contact :zhanjixun@qq.com
 */
@Log4j
public class CookiesStoreImpl implements CookiesStore {

    private final List<Cookie> cookies = Lists.newArrayList();
    //读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void addCookies(List<Cookie> cookie) {
        if (CollectionUtils.isNotEmpty(cookie)) {
            lock.writeLock().lock();
            try {
                cookies.addAll(cookie);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            lock.writeLock().lock();
            try {
                cookies.add(cookie);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public boolean remove(Cookie cookie) {
        if (cookie != null) {
            lock.writeLock().lock();
            try {
                return cookies.remove(cookie);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return false;
    }

    @Override
    public List<Cookie> getCookies() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(cookies);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean clearExpired() {
        return clearExpired(new Date());
    }

    @Override
    public boolean clearExpired(Date date) {
        if (date == null) {
            return false;
        }
        lock.writeLock().lock();
        try {
            List<Cookie> waitRemove = cookies.stream().filter(Cookie::isExpired).collect(Collectors.toList());
            waitRemove.forEach(cookies::remove);
            return CollectionUtils.isNotEmpty(waitRemove);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clearCookies() {
        lock.writeLock().lock();
        try {
            cookies.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void cacheCookie(File cacheFile) {
        if (CollectionUtils.isNotEmpty(getCookies())) {
            try {
                String string = JSON.toJSONString(getCookies());
                //log.info("cache cookie " + string);
                File parentFile = cacheFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                Okio.buffer(Okio.sink(cacheFile)).writeUtf8(string).flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int loadCookieCache(File cacheFile) {
        try {
            String json = Okio.buffer(Okio.source(cacheFile)).readUtf8();
            //log.info("load cookie cache " + json);
            List<Cookie> cookie = JSON.parseArray(json, Cookie.class);
            addCookies(cookie);
            return cookie.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
