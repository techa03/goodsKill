package com.goodskill.common.info;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口响应结果，一般用于controller层
 *
 * @author heng
 * @date 2016/7/23
 */
@Data
public class Result<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 传输的数据
     */
    private transient T data;
    /**
     * 附加信息
     */
    private String message;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Result(String code, boolean success, T data, String message) {
        this.code = code;
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Result(boolean success, T data, String message) {
        this.code = code;
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static Result<?> ok() {
        return new Result<>("200", true, null, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>("200",true, data, null);
    }

    public static <T> Result<T> ok(T data, String message) {
        return new Result<>("200",true, data, message);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>("500",false, null, null);
    }

    public static <T> Result<T> fail() {
        return new Result<>("500",false, null, null);
    }

    public static <T> Result<T> fail(T data, String message) {
        return new Result<>("500",false, data, message);
    }

}
