package org.seckill.exception;

/**
 * Created by heng on 2016/7/16.
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillException(String message) {
        super(message);
    }
}
