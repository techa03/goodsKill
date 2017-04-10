package org.seckill.exception;

/**
 * Created by heng on 2016/7/16.
 */
public class SeckillException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8181529577964661145L;

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillException(String message) {
		super(message);
	}
}
