package org.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.seckill.api.service.UserRoleService;
import org.seckill.entity.UserRole;
import org.seckill.mp.dao.mapper.UserRoleMapper;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public boolean remove(UserRole entity) {
        return super.remove(new QueryWrapper<>(entity));
    }

    @Override
    public List<UserRole> list() {
        return super.list();
    }

    @Override
    public List<UserRole> list(Integer userId) {
        UserRole query = new UserRole();
        query.setUserId(userId);
        return super.list(new QueryWrapper<>(query));
    }
}
