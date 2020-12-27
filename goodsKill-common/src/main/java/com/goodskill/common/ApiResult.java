package com.goodskill.common;

import java.io.Serializable;

/**
 * @author techa03
 * @date 2020-08-02
 */
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = -4696898674758059398L;

    /**
     * 结果代码
     */
    private int code;
    /**
     * 错误说明
     */
    private String msg;
    /**
     * 结果对象
     */
    private T data;
    /**
     * 是否成功
     */
    private boolean isSuccess;

    /**
     * 版本
     */
    private String version;

    public ApiResult() {
        this.setCode(ResultCode.C200.getCode());
        this.setMsg(ResultCode.C200.getDesc());
        this.setSuccess(true);
        this.setVersion(ResultCode.C606.getDesc());
    }


    public ApiResult(int code, String message, boolean success, T dataMap, String version) {
        this.setCode(code);
        this.setMsg(message);
        this.setSuccess(success);
        this.setData(dataMap);
        this.setVersion(version);
    }

    public static <T> ApiResult<T> newInstance(ResultCode resultCode, boolean success, T value, String version) {
        return new ApiResult(resultCode.code, resultCode.getDesc(), success, value, version);
    }

    public static <T> ApiResult<T> ok(T value, String message) {
        return new ApiResult(ResultCode.C200.getCode(), message, true, (Object) null, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> ok() {
        return new ApiResult(ResultCode.C200.getCode(), ResultCode.C200.getDesc(), true, (Object) null, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> ok(T value) {
        return new ApiResult(ResultCode.C200.getCode(), ResultCode.C200.getDesc(), true, value, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> error(ResultCode errorCode) {
        return new ApiResult(errorCode.getCode(), errorCode.getDesc(), false, (Object) null, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult(code, message, false, (Object) null, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> error(int code, String message, Object result) {
        return new ApiResult(code, message, false, result, ResultCode.C606.getDesc());
    }

    public static <T> ApiResult<T> error(int code, String message, Object result, String version) {
        return new ApiResult(code, message, false, result, version);
    }

    public ApiResult<T> setErrorCode(ResultCode errorCode) {
        if (errorCode == null) {
            return null;
        } else {
            this.code = errorCode.getCode();
            this.msg = errorCode.getDesc();
            this.isSuccess = false;
            this.version = ResultCode.C606.getDesc();
            return this;
        }
    }

    public ApiResult<T> setErrorCode(ResultCode errorCode, Object... args) {
        if (errorCode == null) {
            return null;
        } else {
            this.code = errorCode.code;
            this.setFormatMessage(errorCode.getDesc(), args);
            this.isSuccess = false;
            this.version = ResultCode.C606.getDesc();
            return this;
        }
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setFormatMessage(String message, Object... args) {
        if (args != null && args.length != 0) {
            this.setMsg(String.format(message, args));
        } else {
            this.setMsg(message);
        }

    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
