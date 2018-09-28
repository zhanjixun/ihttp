package com.zhanjixun.ihttp;

import lombok.Data;

import java.util.Date;

/**
 * @author :zhanjixun
 * @date : 2018/9/28 10:13
 */
@Data
public class ICookie {

    private String cookieComment;

    private String cookieDomain;

    private Date cookieExpiryDate;

    private String cookiePath;

    private boolean isSecure;

    private boolean hasPathAttribute = false;

    private boolean hasDomainAttribute = false;

    private int cookieVersion = 0;

}
