package org.seckill.api.user.bo;

import lombok.Data;
import org.seckill.entity.User;

@Data
public class UserBo extends User {
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
