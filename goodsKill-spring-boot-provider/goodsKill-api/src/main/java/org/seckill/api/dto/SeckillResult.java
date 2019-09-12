package org.seckill.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author heng
 * @date 2016/7/23
 */
@Data
public class SeckillResult<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean success;
    private transient T data;
    private String error;

    public SeckillResult(boolean success, T data) {

        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {

        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
