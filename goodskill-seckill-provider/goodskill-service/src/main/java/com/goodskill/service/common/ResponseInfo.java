package com.goodskill.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务器响应码定义
 *
 * @author heng
 */
@AllArgsConstructor
@Getter
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

}
