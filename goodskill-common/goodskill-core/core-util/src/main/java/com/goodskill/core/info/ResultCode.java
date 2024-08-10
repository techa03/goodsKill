package com.goodskill.core.info;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统响应码
 *
 * @since 2020-08-02
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    /**
     *
     */
    C200(200, "成功"),
    C201(201, "生效数据需同步"),
    C400(400, "数据错误"),
    C401(401, "登录超时"),
    C403(403, "拒绝访问"),
    C404(404, "没有发现"),

    /**
     * 406-参数异常
     */
    C406(406, "参数异常"),
    C415(415, "不支持Media Type"),
    C500(500, "内部错误"),
    C501(501, "数据库操作失败"),
    C502(502, "数据库新增失败"),
    C503(503, "数据库修改失败"),
    C504(504, "数据库删除失败"),

    /**
     * 自定义业务异常
     */


    /**
     * 版本
     */
    C606(606, "1.0");


    final int code;
    final String desc;

    public static ResultCode format(String name) {
        for (ResultCode value : ResultCode.values()) {
            if (name.equals(value.toString())) {
                return value;
            }
        }
        return null;

    }

}
