package com.goodskill.api.bo;

import com.goodskill.entity.User;
import lombok.Data;

@Data
public class UserBO extends User {
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
