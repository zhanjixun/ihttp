package com.zhanjixun.ihttp.cookie;

import com.alibaba.fastjson.JSON;
import com.zhanjixun.ihttp.CookiesStore;
import com.zhanjixun.ihttp.utils.Util;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
public class CookiesStoreImpl implements CookiesStore {

    private final Set<Cookie> cookies = new TreeSet<>((c1, c2) -> {
        int res = c1.getName().compareTo(c2.getName());
        if (res == 0) {
            String d1 = c1.getDomain();
            if (d1 == null) {
                d1 = "";
            } else if (d1.indexOf('.') == -1) {
                d1 = d1 + ".local";
            }
            String d2 = c2.getDomain();
            if (d2 == null) {
                d2 = "";
            } else if (d2.indexOf('.') == -1) {
                d2 = d2 + ".local";
            }
            res = d1.compareToIgnoreCase(d2);
        }
        if (res == 0) {
            String p1 = c1.getPath();
            if (p1 == null) {
                p1 = "/";
            }
            String p2 = c2.getPath();
            if (p2 == null) {
                p2 = "/";
            }
            res = p1.compareTo(p2);
        }
        return res;
    });

    //读写锁
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void addCookies(List<Cookie> cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                addCookie(cookie);
            }
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            lock.writeLock().lock();
            try {
                cookies.remove(cookie);
                if (!cookie.isExpired()) {
                    cookies.add(cookie);
                }
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
            List<Cookie> waitRemove = cookies.stream().filter(d -> d.isExpired(date)).collect(Collectors.toList());
            for (Cookie cookie : waitRemove) {
                cookies.remove(cookie);
            }
            return Util.isNotEmpty(waitRemove);
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
        if (Util.isNotEmpty(getCookies())) {
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
