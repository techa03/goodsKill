package com.goodskill.core.exception;

import java.io.Serial;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
public class SeckillCloseException extends SeckillException {
	@Serial
	private static final long serialVersionUID = -850110949024776554L;

	public SeckillCloseException(String message) {
		super(message);
	}

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}
}
