package org.seckill.web.constant;

/**
 * 服务器响应码定义
 *
 * @author heng
 */
public enum ResponseInfo {
    /**
     *
     */
    SUCCESS("0000", "响应成功"),
    FAIL("9999", "响应失败"),
    INNER_FAIL("9001", "服务器内部错误"),
    TIME_OUT("0001", "服务器处理超时");
    private String code;
    private String message;

    ResponseInfo(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
