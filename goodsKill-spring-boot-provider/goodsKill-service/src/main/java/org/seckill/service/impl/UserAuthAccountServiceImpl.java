package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.seckill.api.service.UserAuthAccountService;
import org.seckill.api.user.bo.UserBo;
import org.seckill.entity.User;
import org.seckill.entity.UserAuthAccount;
import org.seckill.mp.dao.mapper.UserAuthAccountMapper;
import org.seckill.service.common.UserService;
import org.springframework.beans.BeanUtils;

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
    public UserBo findByThirdAccount(String account, String sourceType) {
        UserBo userBo = new UserBo();
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
