package com.goodskill.core.exception;

import java.io.Serial;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
public class SeckillException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -8181529577964661145L;

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillException(String message) {
		super(message);
	}
}
