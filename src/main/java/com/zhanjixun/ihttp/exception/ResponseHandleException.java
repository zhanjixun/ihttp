package com.zhanjixun.ihttp.exception;

/**
 * 返回值处理异常
 *
 * @author zhanjixun
 * @date 2020-06-11 14:49:17
 */
public class ResponseHandleException extends IHTTPException {

    private static final long serialVersionUID = -5343784840292345261L;

    public ResponseHandleException() {
    }

    public ResponseHandleException(String message) {
        super(message);
    }

    public ResponseHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseHandleException(Throwable cause) {
        super(cause);
    }

    public ResponseHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
