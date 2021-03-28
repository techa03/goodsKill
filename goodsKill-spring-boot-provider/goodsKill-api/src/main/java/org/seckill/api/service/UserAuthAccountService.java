package org.seckill.api.service;

import org.seckill.api.user.bo.UserBo;

/**
 * <p>
 * 用户授权账号表 服务类
 * </p>
 *
 * @author heng
 * @since 2021-03-27
 */
public interface UserAuthAccountService {

    /**
     * 获取用户账户信息
     *
     * @param account    用户名
     * @param sourceType 授权来源
     * @return 用户信息
     */
    UserBo findByThirdAccount(String account, String sourceType);

    /**
     * 校验系统是否存在该第三方账号信息
     *
     * @param account    第三方账号名
     * @param sourceType 来源，如Github
     * @return true 存在
     */
    Boolean ifThirdAccountExists(String account, String sourceType);


}
