package org.seckill.exception;

public class HengException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4816083750833621384L;

	public HengException(String message, Throwable cause) {
		super(message, cause);
	}

	public HengException(String message) {
		super(message);
	}

}
