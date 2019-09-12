package org.seckill.api.exception;

/**
 * 自定义普通异常
 * @author heng
 */
public class CommonException extends RuntimeException {

	public CommonException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommonException(String message) {
		super(message);
	}

}
