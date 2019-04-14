package com.zhanjixun.ihttp.utils;

import com.zhanjixun.ihttp.domain.Cookie;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author :zhanjixun
 * @date : 2018/12/5 10:31
 */
public class CookieUtils {

    public static <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public org.apache.commons.httpclient.Cookie commonsConvert(Cookie originCookie) {
        return copyProperties(originCookie, new org.apache.commons.httpclient.Cookie());
    }

    public Cookie commonsConvert(org.apache.commons.httpclient.Cookie originCookie) {
        return copyProperties(originCookie, new Cookie());
    }

    public static Cookie componentsConvert(org.apache.http.cookie.Cookie originCookie) {
        return null;
    }

    public static org.apache.http.cookie.Cookie componentsConvert(Cookie originCookie) {
        return null;
    }

    public static Cookie okhttpConvert(okhttp3.Cookie originCookie) {
        Cookie iCookie = new Cookie();
        iCookie.setDomain(originCookie.domain());
        iCookie.setPath(originCookie.path());
        iCookie.setName(originCookie.name());
        iCookie.setValue(originCookie.value());
        iCookie.setExpiryDate(new Date(originCookie.expiresAt()));
        iCookie.setSecure(originCookie.secure());
        iCookie.setHttpOnly(originCookie.httpOnly());
        return iCookie;
    }

    public static okhttp3.Cookie okhttpConvert(Cookie originCookie) {
        okhttp3.Cookie.Builder build = new okhttp3.Cookie.Builder();
        build.domain(originCookie.getDomain());
        build.path(originCookie.getPath());
        build.name(originCookie.getName());
        build.value(originCookie.getValue());
        build.expiresAt(originCookie.getExpiryDate().getTime());
        if (originCookie.isSecure()) {
            build.secure();
        }
        if (originCookie.isHttpOnly()) {
            build.httpOnly();
        }
        return build.build();
    }
}