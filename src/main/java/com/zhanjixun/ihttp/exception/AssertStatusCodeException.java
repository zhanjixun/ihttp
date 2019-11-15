package com.zhanjixun.ihttp.exception;

/**
 * @author :zhanjixun
 * @date : 2019/04/26 16:47
 * @contact :zhanjixun@qq.com
 */
public class AssertStatusCodeException extends Exception {

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
