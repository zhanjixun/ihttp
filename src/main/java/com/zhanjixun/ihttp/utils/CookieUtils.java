package com.zhanjixun.ihttp.utils;

import com.zhanjixun.ihttp.domain.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author :zhanjixun
 * @date : 2018/12/5 10:31
 */
public class CookieUtils {

    private static <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static org.apache.commons.httpclient.Cookie commonsConvert(Cookie originCookie) {
        return copyProperties(originCookie, new org.apache.commons.httpclient.Cookie());
    }

    public static Cookie commonsConvert(org.apache.commons.httpclient.Cookie originCookie) {
        return copyProperties(originCookie, new Cookie());
    }

    public static Cookie componentsConvert(org.apache.http.cookie.Cookie originCookie) {
        return copyProperties(originCookie, new Cookie());
    }

    public static org.apache.http.cookie.Cookie componentsConvert(Cookie originCookie) {
        return copyProperties(originCookie, new BasicClientCookie(originCookie.getName(), originCookie.getValue()));
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
