package com.zhanjixun.ihttp.logging;

/**
 * 请求 日志
 *
 * @author zhanjixun
 */
public interface Log {

    String toLogString(ConnectionInfo connectionState);

}
