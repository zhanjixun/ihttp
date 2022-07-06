package com.zhanjixun.ihttp;

/**
 * @author zhanjixun
 * @date 2022-07-06 11:11
 */
public interface RequestInterceptor {

    void apply(RequestTemplate template);
    
}
