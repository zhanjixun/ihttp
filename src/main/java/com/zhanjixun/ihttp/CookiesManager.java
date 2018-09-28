package com.zhanjixun.ihttp;

/**
 * cookie管理
 *
 * @author :zhanjixun
 * @date : 2018/9/28 13:41
 */
public interface CookiesManager {

    void addCookie(ICookie cookie);

    ICookie[] getCookies();

}
