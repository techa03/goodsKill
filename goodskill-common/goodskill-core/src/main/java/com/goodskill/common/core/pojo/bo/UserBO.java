package com.goodskill.common.core.pojo.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6359738517769849900L;

    private Integer id;

    private String account;

    private String password;

    private String username;

    private Integer locked;

    private String avatar;

    private Date lastLoginTime;

    private String mobile;

    private String emailAddr;

    /**
     * 第三方账号id
     */
    private String thirdAccountId;

    /**
     * 关联的第三方账户名称
     */
    private String thirdAccountName;

    /**
     * 第三方授权来源
     */
    private String sourceType;

}
