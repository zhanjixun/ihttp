package com.zhanjixun.ihttp;

import com.zhanjixun.ihttp.domain.Cookie;

import java.util.List;

/**
 * cookie管理
 *
 * @author :zhanjixun
 * @date : 2018/9/28 13:41
 * @see org.springframework.beans.BeanUtils
 */
public interface CookiesManager {
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
     * 获取所有cookie
     *
     * @return 所有cookie
     */
    List<Cookie> getCookies();

    /**
     * 清除所有cookie
     */
    void clearCookies();
}
