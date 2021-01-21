package com.zhanjixun.ihttp.exception;

/**
 * 判断状态码异常
 *
 * @author :zhanjixun
 * @date : 2019/04/26 16:47
 * @contact :zhanjixun@qq.com
 */
public class AssertStatusCodeException extends IHTTPException {

    private static final long serialVersionUID = -913646594083321882L;

    public AssertStatusCodeException() {

    }

    public AssertStatusCodeException(String message) {
        super(message);
    }

    public AssertStatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertStatusCodeException(Throwable cause) {
        super(cause);
    }
}
