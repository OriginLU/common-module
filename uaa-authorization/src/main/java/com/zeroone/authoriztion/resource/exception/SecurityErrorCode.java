package com.zeroone.authoriztion.resource.exception;

/**
 *  异常错误枚举
 */
public enum SecurityErrorCode {
	error(-1, ""),
	login_error(1001, "登录失败"),
	http_requset_error(1002, "请求失败"),
	base_data_error(1003, "基础数据缺失"),
	;

	SecurityErrorCode(int code, String message) {
		this.code = code + offset;
		this.message = message;
	}

	private int code;
	private String message;
	private static final int offset = 50000;

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
