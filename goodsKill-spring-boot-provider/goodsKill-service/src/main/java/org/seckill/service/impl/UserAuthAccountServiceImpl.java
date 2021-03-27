package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.seckill.api.service.UserAuthAccountService;
import org.seckill.entity.User;
import org.seckill.entity.UserAuthAccount;
import org.seckill.mp.dao.mapper.UserAuthAccountMapper;
import org.seckill.service.mp.UserService;

import javax.annotation.Resource;

/**
 * <p>
 * 用户授权账号表 服务实现类
 * </p>
 *
 * @author heng
 * @since 2021-03-27
 */
@DubboService
public class UserAuthAccountServiceImpl extends ServiceImpl<UserAuthAccountMapper, UserAuthAccount> implements UserAuthAccountService {
    @Resource
    private UserService userService;

    @Override
    public User findByThirdAccount(String account, String sourceType) {
        UserAuthAccount userAuthAccount = baseMapper.selectOne(new LambdaQueryWrapper<UserAuthAccount>()
                .eq(UserAuthAccount::getThirdAccountName, account)
                .eq(UserAuthAccount::getSourceType, sourceType).select(UserAuthAccount::getUserId));
        return userService.getById(userAuthAccount.getUserId());
    }

    @Override
    public Boolean ifThirdAccountExists(String account, String sourceType) {
        return baseMapper.selectCount(new LambdaQueryWrapper<UserAuthAccount>()
                .eq(UserAuthAccount::getThirdAccountName, account)
                .eq(UserAuthAccount::getSourceType, sourceType)) == 1;
    }
}
