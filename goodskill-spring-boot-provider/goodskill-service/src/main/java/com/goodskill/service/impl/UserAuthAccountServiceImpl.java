package com.goodskill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.api.bo.UserBO;
import com.goodskill.entity.User;
import com.goodskill.entity.UserAuthAccount;
import com.goodskill.service.common.UserService;
import com.goodskill.service.inner.UserAuthAccountService;
import com.goodskill.service.mapper.UserAuthAccountMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户授权账号表 服务实现类
 * </p>
 *
 * @author heng
 * @since 2021-03-27
 */
@Service
public class UserAuthAccountServiceImpl extends ServiceImpl<UserAuthAccountMapper, UserAuthAccount> implements UserAuthAccountService {
    @Resource
    private UserService userService;
    @Resource
    private UserAuthAccountMapper baseMapper;

    @Override
    public UserBO findByThirdAccount(String account, String sourceType) {
        UserBO userBo = new UserBO();
        UserAuthAccount userAuthAccount = baseMapper.selectOne(new LambdaQueryWrapper<UserAuthAccount>()
                .eq(UserAuthAccount::getThirdAccountName, account)
                .eq(UserAuthAccount::getSourceType, sourceType));
        User user = userService.getById(userAuthAccount.getUserId());
        BeanUtils.copyProperties(user, userBo);
        userBo.setThirdAccountId(userAuthAccount.getThirdAccountId());
        userBo.setSourceType(userAuthAccount.getSourceType());
        userBo.setThirdAccountName(userAuthAccount.getThirdAccountName());
        return userBo;
    }

    @Override
    public Boolean ifThirdAccountExists(String account, String sourceType) {
        return baseMapper.selectCount(new LambdaQueryWrapper<UserAuthAccount>()
                .eq(UserAuthAccount::getThirdAccountName, account)
                .eq(UserAuthAccount::getSourceType, sourceType)) == 1;
    }
}
