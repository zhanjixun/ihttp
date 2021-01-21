package com.zhanjixun.ihttp.exception;

/**
 * 框架顶级异常类
 *
 * @author zhanjixun
 * @date 2020-06-11 14:47:51
 */
public class IHTTPException extends Exception {

    private static final long serialVersionUID = -183501277046490110L;

    public IHTTPException() {
    }

    public IHTTPException(String message) {
        super(message);
    }

    public IHTTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public IHTTPException(Throwable cause) {
        super(cause);
    }

    public IHTTPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
