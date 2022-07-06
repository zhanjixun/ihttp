package com.zhanjixun.ihttp.cookie;

import java.util.Date;

public class Cookie {

    private String name;

    private String value;

    private String domain;

    private String path;

    private Date expiryDate;

    private String comment;

    private boolean isSecure;

    private int version;

    private boolean httpOnly;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getComment() {
        return comment;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public int getVersion() {
        return version;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isExpired() {
        return isExpired(new Date());
    }

    public boolean isExpired(Date date) {
        return expiryDate != null && expiryDate.before(date);
    }
}
