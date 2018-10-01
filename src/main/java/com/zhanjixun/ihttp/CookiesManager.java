package com.zhanjixun.ihttp;

/**
 * cookie管理
 *
 * @author :zhanjixun
 * @date : 2018/9/28 13:41
 */
public interface CookiesManager {
    /**
     * 增加cookie
     *
     * @param cookie 需要增加的cookie
     */
    void addCookie(ICookie cookie);

    /**
     * 获取所有cookie
     *
     * @return 所有cookie
     */
    ICookie[] getCookies();

}
