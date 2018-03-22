package com.zhanjixun.ihttp.constant;

/**
 * HTTP请求方法
 *
 * @author zhanjixun
 */
public enum HttpMethod {

    GET("GET"), POST("POST"), DELETE("DELETE"), HEAD("HEAD"), PUT("PUT");

    private String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
