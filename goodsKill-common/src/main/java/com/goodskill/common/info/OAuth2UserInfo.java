package com.goodskill.common.info;

import lombok.Data;

/**
 * 用户授权信息
 *
 * @author heng
 * @since 2021/3/27
 */
@Data
public class OAuth2UserInfo {
    /**
     * 账户名
     */
    private String account;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户唯一标识
     */
    private String userId;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 授权来源网站
     */
    private String sourceType;

    /**
     * 用户账号创建时间
     */
    private String createTime;

    /**
     * 生日
     */
    private String birthday;

}
