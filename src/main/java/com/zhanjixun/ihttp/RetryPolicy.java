package com.zhanjixun.ihttp;

/**
 * 重试策略
 *
 * @author :zhanjixun
 * @date : 2019/11/20 22:10
 */
@FunctionalInterface
public interface RetryPolicy {

    /**
     * 是否需要重试
     *
     * @param response 接口请求返回值
     * @return 是否需要重试
     */
    boolean needRetry(Response response);

}
