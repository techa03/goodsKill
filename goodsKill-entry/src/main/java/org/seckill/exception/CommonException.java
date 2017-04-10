package org.seckill.exception;

public class CommonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4816083750833621384L;

	public CommonException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommonException(String message) {
		super(message);
	}

}
