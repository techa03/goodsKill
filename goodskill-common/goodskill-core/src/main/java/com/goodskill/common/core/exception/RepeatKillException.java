package com.goodskill.common.core.exception;

import java.io.Serial;

/**
 *
 * @author heng
 * @date 2016/7/16
 */
public class RepeatKillException extends SeckillException {

	@Serial
	private static final long serialVersionUID = -7588706798824576584L;

	public RepeatKillException(String message) {
		super(message);
	}

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}
}
